const express = require('express');
const router = express.Router();
const authMiddleware = require('../middlewares/auth.middleware');
const solicitudesController = require('../controllers/solicitudes.controller');

router.post('/', authMiddleware, solicitudesController.createSolicitud);
router.get('/mis-solicitudes', authMiddleware, solicitudesController.getMisSolicitudes);
router.get('/:id', authMiddleware, solicitudesController.getSolicitudById);

module.exports = router;
