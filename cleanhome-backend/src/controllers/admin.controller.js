const pool = require('../config/db');

// Estados permitidos por la regla de negocio.
const ESTADOS_VALIDOS = ['Pendiente', 'Confirmada', 'En proceso', 'Completada', 'Cancelada'];

// Lista todas las solicitudes para gestion administrativa.
const getAllSolicitudes = async (req, res) => {
  try {
    const [rows] = await pool.query(
      `SELECT s.*, u.nombre as cliente, u.correo as correo_cliente, sv.nombre as servicio, p.nombre as personal_nombre
       FROM solicitudes s
       JOIN usuarios u ON s.id_usuario = u.id_usuario
       JOIN servicios sv ON s.id_servicio = sv.id_servicio
       LEFT JOIN personal_limpieza p ON s.id_personal = p.id_personal
       ORDER BY s.fecha_creacion DESC`
    );

    res.json({ solicitudes: rows });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener las solicitudes.' });
  }
};

// Lista personal disponible/no disponible para asignaciones.
const getPersonalLimpieza = async (req, res) => {
  try {
    const [personal] = await pool.query(
      `SELECT id_personal, nombre, telefono, disponible
       FROM personal_limpieza
       ORDER BY disponible DESC, nombre ASC`
    );

    res.json({ personal });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener personal de limpieza.' });
  }
};

// Cambia el estado de una solicitud.
const updateEstado = async (req, res) => {
  try {
    const { id } = req.params;
    const { estado } = req.body;

    if (!estado) {
      return res.status(400).json({ message: 'El campo estado es obligatorio.' });
    }

    const estadoNormalizado = String(estado).trim();
    // Valida que solo se usen estados oficiales.
    if (!ESTADOS_VALIDOS.includes(estadoNormalizado)) {
      return res.status(400).json({
        message: `Estado no valido. Estados permitidos: ${ESTADOS_VALIDOS.join(', ')}.`,
      });
    }

    const [result] = await pool.query(
      'UPDATE solicitudes SET estado = ?, updated_at = CURRENT_TIMESTAMP WHERE id_solicitud = ?',
      [estadoNormalizado, id]
    );
    if (result.affectedRows === 0) {
      return res.status(404).json({ message: 'Solicitud no encontrada.' });
    }

    res.json({ message: 'Estado de la solicitud actualizado correctamente.' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al actualizar el estado de la solicitud.' });
  }
};

// Asigna un miembro de personal a una solicitud.
const assignPersonal = async (req, res) => {
  try {
    const { id } = req.params;
    const { id_personal } = req.body;

    if (!id_personal) {
      return res.status(400).json({ message: 'El campo id_personal es obligatorio.' });
    }

    const [personalRows] = await pool.query('SELECT id_personal FROM personal_limpieza WHERE id_personal = ?', [id_personal]);
    if (personalRows.length === 0) {
      return res.status(400).json({ message: 'Personal de limpieza no encontrado.' });
    }

    const [result] = await pool.query(
      'UPDATE solicitudes SET id_personal = ?, updated_at = CURRENT_TIMESTAMP WHERE id_solicitud = ?',
      [id_personal, id]
    );
    if (result.affectedRows === 0) {
      return res.status(404).json({ message: 'Solicitud no encontrada.' });
    }

    res.json({ message: 'Personal asignado correctamente.' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al asignar personal a la solicitud.' });
  }
};

// Lista servicios (activos e inactivos) para panel admin.
const getServiciosAdmin = async (req, res) => {
  try {
    const [services] = await pool.query('SELECT * FROM servicios ORDER BY nombre');
    res.json({ servicios: services });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener los servicios.' });
  }
};

// Crea un nuevo servicio.
const createServicio = async (req, res) => {
  try {
    const { nombre, descripcion, duracion_estimada, precio, activo } = req.body;

    if (!nombre || precio == null) {
      return res.status(400).json({ message: 'El nombre y el precio son obligatorios.' });
    }

    const [result] = await pool.query(
      `INSERT INTO servicios (nombre, descripcion, duracion_estimada, precio, activo)
       VALUES (?, ?, ?, ?, ?)`,
      [nombre, descripcion || null, duracion_estimada || null, precio, activo == null ? true : activo]
    );

    res.status(201).json({ message: 'Servicio creado correctamente.', id_servicio: result.insertId });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al crear el servicio.' });
  }
};

// Actualiza datos de un servicio existente.
const updateServicio = async (req, res) => {
  try {
    const { id } = req.params;
    const { nombre, descripcion, duracion_estimada, precio, activo } = req.body;

    if (!nombre || precio == null) {
      return res.status(400).json({ message: 'El nombre y el precio son obligatorios.' });
    }

    const [result] = await pool.query(
      `UPDATE servicios
       SET nombre = ?, descripcion = ?, duracion_estimada = ?, precio = ?, activo = ?, updated_at = CURRENT_TIMESTAMP
       WHERE id_servicio = ?`,
      [nombre, descripcion || null, duracion_estimada || null, precio, activo == null ? true : activo, id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: 'Servicio no encontrado.' });
    }

    res.json({ message: 'Servicio actualizado correctamente.' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al actualizar el servicio.' });
  }
};

// Desactivacion logica de servicio (no elimina registro fisico).
const deleteServicio = async (req, res) => {
  try {
    const { id } = req.params;

    const [servicios] = await pool.query('SELECT id_servicio, activo FROM servicios WHERE id_servicio = ?', [id]);
    if (servicios.length === 0) {
      return res.status(404).json({ message: 'Servicio no encontrado.' });
    }

    if (!servicios[0].activo) {
      return res.json({ message: 'Servicio ya estaba desactivado.' });
    }

    await pool.query('UPDATE servicios SET activo = FALSE, updated_at = CURRENT_TIMESTAMP WHERE id_servicio = ?', [id]);
    res.json({ message: 'Servicio desactivado correctamente.' });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al desactivar el servicio.' });
  }
};

module.exports = {
  getAllSolicitudes,
  getPersonalLimpieza,
  updateEstado,
  assignPersonal,
  getServiciosAdmin,
  createServicio,
  updateServicio,
  deleteServicio,
};
