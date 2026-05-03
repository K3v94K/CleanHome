const pool = require('../config/db');

// Crea una nueva solicitud asociada al usuario autenticado.
const createSolicitud = async (req, res) => {
  try {
    const { id_servicio, fecha_servicio, hora_servicio, direccion_atencion } = req.body;
    const id_usuario = req.user.id_usuario;

    // Campos minimos para programar el servicio.
    if (!id_servicio || !fecha_servicio || !hora_servicio || !direccion_atencion) {
      return res.status(400).json({ message: 'id_servicio, fecha_servicio, hora_servicio y direccion_atencion son obligatorios.' });
    }

    // Verifica que el servicio exista y este activo.
    const [services] = await pool.query('SELECT id_servicio FROM servicios WHERE id_servicio = ? AND activo = TRUE', [id_servicio]);
    if (services.length === 0) {
      return res.status(400).json({ message: 'Servicio no válido o no disponible.' });
    }

    const [result] = await pool.query(
      `INSERT INTO solicitudes (id_usuario, id_servicio, fecha_servicio, hora_servicio, direccion_atencion)
       VALUES (?, ?, ?, ?, ?)`,
      [id_usuario, id_servicio, fecha_servicio, hora_servicio, direccion_atencion]
    );

    res.status(201).json({ message: 'Solicitud creada correctamente.', id_solicitud: result.insertId });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al crear la solicitud.' });
  }
};

// Lista historial de solicitudes del usuario autenticado.
const getMisSolicitudes = async (req, res) => {
  try {
    const id_usuario = req.user.id_usuario;
    const [rows] = await pool.query(
      `SELECT s.*, u.nombre as cliente, sv.nombre as servicio, sv.descripcion AS servicio_descripcion, p.nombre AS personal_nombre
       FROM solicitudes s
       JOIN usuarios u ON s.id_usuario = u.id_usuario
       JOIN servicios sv ON s.id_servicio = sv.id_servicio
       LEFT JOIN personal_limpieza p ON s.id_personal = p.id_personal
       WHERE s.id_usuario = ?
       ORDER BY s.fecha_creacion DESC`,
      [id_usuario]
    );
    res.json({ solicitudes: rows });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener las solicitudes.' });
  }
};

// Devuelve una solicitud puntual validando propietario.
const getSolicitudById = async (req, res) => {
  try {
    const { id } = req.params;
    const id_usuario = req.user.id_usuario;

    const [rows] = await pool.query(
      `SELECT s.*, u.nombre as cliente, sv.nombre as servicio, sv.descripcion AS servicio_descripcion, p.nombre AS personal_nombre
       FROM solicitudes s
       JOIN usuarios u ON s.id_usuario = u.id_usuario
       JOIN servicios sv ON s.id_servicio = sv.id_servicio
       LEFT JOIN personal_limpieza p ON s.id_personal = p.id_personal
       WHERE s.id_solicitud = ?`,
      [id]
    );

    if (rows.length === 0) {
      return res.status(404).json({ message: 'Solicitud no encontrada.' });
    }

    const solicitud = rows[0];
    // Impide leer solicitudes de otros clientes.
    if (solicitud.id_usuario !== id_usuario) {
      return res.status(403).json({ message: 'No tienes permiso para ver esta solicitud.' });
    }

    res.json({ solicitud });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener la solicitud.' });
  }
};

module.exports = {
  createSolicitud,
  getMisSolicitudes,
  getSolicitudById,
};
