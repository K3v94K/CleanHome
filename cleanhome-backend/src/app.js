const express = require('express');
const cors = require('cors');
const authRoutes = require('./routes/auth.routes');
const serviciosRoutes = require('./routes/servicios.routes');
const solicitudesRoutes = require('./routes/solicitudes.routes');
const adminRoutes = require('./routes/admin.routes');

const app = express();

// Habilita CORS para permitir consumo desde frontend web/movil.
app.use(cors());
// Permite recibir JSON en req.body.
app.use(express.json());

// Rutas de autenticacion (registro/login).
app.use('/api/auth', authRoutes);
// Rutas publicas de catalogo de servicios.
app.use('/api/servicios', serviciosRoutes);
// Rutas de cliente autenticado para crear/ver solicitudes.
app.use('/api/solicitudes', solicitudesRoutes);
// Rutas exclusivas de administracion.
app.use('/api/admin', adminRoutes);

// Fallback para endpoints no definidos.
app.use((req, res) => {
  res.status(404).json({ message: 'Endpoint no encontrado' });
});

// Manejador global de errores no controlados.
app.use((err, req, res, next) => {
  console.error(err);
  res.status(500).json({ message: 'Error interno del servidor' });
});

module.exports = app;
