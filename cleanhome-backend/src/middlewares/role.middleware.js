const requireRole = (roleName) => {
  // Middleware de autorizacion por rol.
  return (req, res, next) => {
    if (!req.user || req.user.nombre_rol !== roleName) {
      return res.status(403).json({ message: 'Acceso denegado. Se requiere rol de administrador.' });
    }
    next();
  };
};

module.exports = requireRole;
