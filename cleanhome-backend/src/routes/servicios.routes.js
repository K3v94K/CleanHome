const express = require('express');
const router = express.Router();
const serviciosController = require('../controllers/servicios.controller');

router.get('/', serviciosController.getServicios);
router.get('/:id', serviciosController.getServicioById);

module.exports = router;
