const express = require('express');
const router = express.Router();
const authMiddleware = require('../middlewares/auth.middleware');
const requireRole = require('../middlewares/role.middleware');
const adminController = require('../controllers/admin.controller');

router.use(authMiddleware);
router.use(requireRole('Admin'));

router.get('/solicitudes', adminController.getAllSolicitudes);
router.patch('/solicitudes/:id/estado', adminController.updateEstado);
router.patch('/solicitudes/:id/asignar-personal', adminController.assignPersonal);

router.get('/servicios', adminController.getServiciosAdmin);
router.post('/servicios', adminController.createServicio);
router.put('/servicios/:id', adminController.updateServicio);
router.delete('/servicios/:id', adminController.deleteServicio);

module.exports = router;
