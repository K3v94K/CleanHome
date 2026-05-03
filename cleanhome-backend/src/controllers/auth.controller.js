const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const pool = require('../config/db');

// Registra usuarios cliente.
const register = async (req, res) => {
  try {
    const { nombre, correo, telefono, direccion, password } = req.body;

    // Validaciones minimas obligatorias.
    if (!nombre || !correo || !password) {
      return res.status(400).json({ message: 'Nombre, correo y contraseña son obligatorios.' });
    }

    // Evita registrar dos cuentas con el mismo correo.
    const [existingUsers] = await pool.query('SELECT id_usuario FROM usuarios WHERE correo = ?', [correo]);
    if (existingUsers.length > 0) {
      return res.status(400).json({ message: 'El correo ya está registrado.' });
    }

    // Obtiene id del rol Cliente.
    const [roles] = await pool.query('SELECT id_rol FROM roles WHERE nombre_rol = ?', ['Cliente']);
    if (roles.length === 0) {
      return res.status(500).json({ message: 'Rol Cliente no encontrado en la base de datos.' });
    }

    // Se guarda hash, no password en texto plano.
    const password_hash = await bcrypt.hash(password, 10);
    const [result] = await pool.query(
      `INSERT INTO usuarios (nombre, correo, telefono, direccion, password_hash, id_rol)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [nombre, correo, telefono || null, direccion || null, password_hash, roles[0].id_rol]
    );

    res.status(201).json({ message: 'Usuario registrado correctamente.', id_usuario: result.insertId });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error interno al registrar usuario.' });
  }
};

// Inicia sesion y entrega un JWT.
const login = async (req, res) => {
  try {
    const { correo, password } = req.body;

    if (!correo || !password) {
      return res.status(400).json({ message: 'Correo y contraseña son obligatorios.' });
    }

    const [rows] = await pool.query(
      `SELECT u.id_usuario, u.nombre, u.correo, u.password_hash, r.id_rol, r.nombre_rol
       FROM usuarios u
       JOIN roles r ON u.id_rol = r.id_rol
       WHERE u.correo = ?`,
      [correo]
    );

    if (rows.length === 0) {
      return res.status(401).json({ message: 'Credenciales inválidas.' });
    }

    const user = rows[0];
    // Compara password contra hash guardado.
    const validPassword = await bcrypt.compare(password, user.password_hash);
    if (!validPassword) {
      return res.status(401).json({ message: 'Credenciales inválidas.' });
    }

    // Firma token con datos del usuario y rol.
    const token = jwt.sign(
      {
        id_usuario: user.id_usuario,
        nombre: user.nombre,
        correo: user.correo,
        id_rol: user.id_rol,
        nombre_rol: user.nombre_rol,
      },
      process.env.JWT_SECRET,
      { expiresIn: process.env.JWT_EXPIRES_IN || '8h' }
    );

    res.json({
      message: 'Inicio de sesión exitoso.',
      token,
      usuario: {
        id_usuario: user.id_usuario,
        nombre: user.nombre,
        correo: user.correo,
        id_rol: user.id_rol,
        nombre_rol: user.nombre_rol,
      },
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error interno al iniciar sesión.' });
  }
};

module.exports = {
  register,
  login,
};
