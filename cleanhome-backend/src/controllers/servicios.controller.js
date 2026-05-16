const pool = require('../config/db');

// Lista solo servicios activos para mostrar al cliente.
const getServicios = async (req, res) => {
  try {
    const { updated_since } = req.query;
    let query = 'SELECT * FROM servicios WHERE activo = TRUE';
    const params = [];

    // Para sincronizacion movil se devuelven cambios, incluyendo servicios desactivados.
    if (updated_since) {
      if (Number.isNaN(Date.parse(updated_since))) {
        return res.status(400).json({ message: 'updated_since debe ser una fecha valida.' });
      }

      query = 'SELECT * FROM servicios WHERE updated_at > ?';
      params.push(updated_since);
    }

    const [services] = await pool.query(`${query} ORDER BY nombre`, params);
    res.json({ servicios: services });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener los servicios.' });
  }
};

// Devuelve detalle de un servicio activo por id.
const getServicioById = async (req, res) => {
  try {
    const { id } = req.params;
    const [services] = await pool.query('SELECT * FROM servicios WHERE id_servicio = ? AND activo = TRUE', [id]);
    if (services.length === 0) {
      return res.status(404).json({ message: 'Servicio no encontrado o no disponible.' });
    }
    res.json({ servicio: services[0] });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener el servicio.' });
  }
};

module.exports = {
  getServicios,
  getServicioById,
};
