const pool = require('../config/db');

const buildSolicitudSelect = () => (
  `SELECT s.*, u.nombre as cliente, sv.nombre as servicio, sv.descripcion AS servicio_descripcion, p.nombre AS personal_nombre
   FROM solicitudes s
   JOIN usuarios u ON s.id_usuario = u.id_usuario
   JOIN servicios sv ON s.id_servicio = sv.id_servicio
   LEFT JOIN personal_limpieza p ON s.id_personal = p.id_personal`
);

// Crea una nueva solicitud asociada al usuario autenticado.
const createSolicitud = async (req, res) => {
  try {
    const { id_servicio, fecha_servicio, hora_servicio, direccion_atencion, client_temp_id } = req.body;
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

    if (client_temp_id) {
      const [existingRequest] = await pool.query(
        'SELECT id_solicitud FROM solicitudes WHERE id_usuario = ? AND client_temp_id = ?',
        [id_usuario, client_temp_id]
      );
      if (existingRequest.length > 0) {
        return res.status(200).json({
          message: 'Solicitud ya sincronizada anteriormente.',
          id_solicitud: existingRequest[0].id_solicitud,
        });
      }
    }

    const [result] = await pool.query(
      `INSERT INTO solicitudes (id_usuario, id_servicio, fecha_servicio, hora_servicio, direccion_atencion, client_temp_id)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [id_usuario, id_servicio, fecha_servicio, hora_servicio, direccion_atencion, client_temp_id || null]
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
    const { updated_since } = req.query;
    const params = [id_usuario];
    let where = 'WHERE s.id_usuario = ?';

    if (updated_since) {
      if (Number.isNaN(Date.parse(updated_since))) {
        return res.status(400).json({ message: 'updated_since debe ser una fecha valida.' });
      }

      where += ' AND s.updated_at > ?';
      params.push(updated_since);
    }

    const [rows] = await pool.query(
      `${buildSolicitudSelect()}
       ${where}
       ORDER BY s.fecha_creacion DESC`,
      params
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
      `${buildSolicitudSelect()}
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

// Recibe solicitudes creadas offline en Android y evita duplicarlas por client_temp_id.
const syncSolicitudes = async (req, res) => {
  try {
    const id_usuario = req.user.id_usuario;
    const { solicitudes } = req.body;

    if (!Array.isArray(solicitudes) || solicitudes.length === 0) {
      return res.status(400).json({ message: 'Debe enviar un arreglo de solicitudes para sincronizar.' });
    }

    const sincronizadas = [];
    const errores = [];

    for (const [index, solicitud] of solicitudes.entries()) {
      const { id_servicio, fecha_servicio, hora_servicio, direccion_atencion, client_temp_id } = solicitud;

      if (!id_servicio || !fecha_servicio || !hora_servicio || !direccion_atencion || !client_temp_id) {
        errores.push({
          index,
          client_temp_id: client_temp_id || null,
          message: 'id_servicio, fecha_servicio, hora_servicio, direccion_atencion y client_temp_id son obligatorios.',
        });
        continue;
      }

      const [existingRequest] = await pool.query(
        'SELECT id_solicitud FROM solicitudes WHERE id_usuario = ? AND client_temp_id = ?',
        [id_usuario, client_temp_id]
      );
      if (existingRequest.length > 0) {
        sincronizadas.push({
          client_temp_id,
          id_solicitud: existingRequest[0].id_solicitud,
          status: 'existing',
        });
        continue;
      }

      const [services] = await pool.query('SELECT id_servicio FROM servicios WHERE id_servicio = ? AND activo = TRUE', [id_servicio]);
      if (services.length === 0) {
        errores.push({
          index,
          client_temp_id,
          message: 'Servicio no valido o no disponible.',
        });
        continue;
      }

      const [result] = await pool.query(
        `INSERT INTO solicitudes (id_usuario, id_servicio, fecha_servicio, hora_servicio, direccion_atencion, client_temp_id)
         VALUES (?, ?, ?, ?, ?, ?)`,
        [id_usuario, id_servicio, fecha_servicio, hora_servicio, direccion_atencion, client_temp_id]
      );

      sincronizadas.push({
        client_temp_id,
        id_solicitud: result.insertId,
        status: 'created',
      });
    }

    res.json({ sincronizadas, errores });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al sincronizar solicitudes.' });
  }
};

module.exports = {
  createSolicitud,
  getMisSolicitudes,
  getSolicitudById,
  syncSolicitudes,
};
