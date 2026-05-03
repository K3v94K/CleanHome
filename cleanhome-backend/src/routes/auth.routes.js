const express = require('express');
const router = express.Router();
const authController = require('../controllers/auth.controller');

// Registro de cliente.
router.post('/register', authController.register);
// Login de usuario (cliente o admin).
router.post('/login', authController.login);

module.exports = router;
