const dotenv = require('dotenv');
const app = require('./app');

// Carga variables de entorno desde .env antes de iniciar el servidor.
dotenv.config();

// Puerto HTTP donde escuchara la API.
const PORT = process.env.PORT || 3000;

// Punto de arranque del backend.
app.listen(PORT, () => {
  console.log(`CleanHome backend listening on port ${PORT}`);
});
