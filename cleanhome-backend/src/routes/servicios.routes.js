const express = require('express');
const router = express.Router();
const serviciosController = require('../controllers/servicios.controller');

// Lista de servicios activos para clientes.
router.get('/', serviciosController.getServicios);
// Detalle de un servicio activo.
router.get('/:id', serviciosController.getServicioById);

module.exports = router;
