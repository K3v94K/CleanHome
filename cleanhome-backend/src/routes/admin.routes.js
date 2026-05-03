const express = require('express');
const router = express.Router();
const authMiddleware = require('../middlewares/auth.middleware');
const requireRole = require('../middlewares/role.middleware');
const adminController = require('../controllers/admin.controller');

// Todo endpoint admin requiere:
// 1) token JWT valido
// 2) rol Admin
router.use(authMiddleware);
router.use(requireRole('Admin'));

// Gestion de solicitudes.
router.get('/solicitudes', adminController.getAllSolicitudes);
router.get('/personal', adminController.getPersonalLimpieza);
router.patch('/solicitudes/:id/estado', adminController.updateEstado);
router.patch('/solicitudes/:id/asignar-personal', adminController.assignPersonal);

// Gestion de servicios.
router.get('/servicios', adminController.getServiciosAdmin);
router.post('/servicios', adminController.createServicio);
router.put('/servicios/:id', adminController.updateServicio);
router.delete('/servicios/:id', adminController.deleteServicio);

module.exports = router;
