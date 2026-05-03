const express = require('express');
const router = express.Router();
const authMiddleware = require('../middlewares/auth.middleware');
const solicitudesController = require('../controllers/solicitudes.controller');

// Crea una solicitud de servicio para el usuario autenticado.
router.post('/', authMiddleware, solicitudesController.createSolicitud);
// Lista todas las solicitudes del usuario autenticado.
router.get('/mis-solicitudes', authMiddleware, solicitudesController.getMisSolicitudes);
// Obtiene una solicitud puntual, validando propietario.
router.get('/:id', authMiddleware, solicitudesController.getSolicitudById);

module.exports = router;
