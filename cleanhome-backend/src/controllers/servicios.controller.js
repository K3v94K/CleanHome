const pool = require('../config/db');

const getServicios = async (req, res) => {
  try {
    const [services] = await pool.query('SELECT * FROM servicios WHERE activo = TRUE ORDER BY nombre');
    res.json({ servicios: services });
  } catch (error) {
    console.error(error);
    res.status(500).json({ message: 'Error al obtener los servicios.' });
  }
};

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
