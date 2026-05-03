const mysql = require('mysql2/promise');
const dotenv = require('dotenv');

// Lee configuracion de conexion desde variables de entorno.
dotenv.config();

// Pool de conexiones para reutilizar conexiones y mejorar rendimiento.
const pool = mysql.createPool({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'CleanHomeDB',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
});

module.exports = pool;
