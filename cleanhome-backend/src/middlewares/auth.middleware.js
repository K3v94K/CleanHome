const jwt = require('jsonwebtoken');

const authMiddleware = (req, res, next) => {
  // Espera encabezado con formato: Authorization: Bearer <token>
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({ message: 'Token requerido.' });
  }

  // Obtiene solo el token JWT.
  const token = authHeader.split(' ')[1];
  if (!token) {
    return res.status(401).json({ message: 'Token mal formado.' });
  }

  try {
    // Valida firma y expiracion del token.
    const payload = jwt.verify(token, process.env.JWT_SECRET);
    // Expone datos del usuario autenticado para siguientes handlers.
    req.user = payload;
    next();
  } catch (error) {
    return res.status(401).json({ message: 'Token inválido o expirado.' });
  }
};

module.exports = authMiddleware;
