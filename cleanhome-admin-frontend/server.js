const http = require('http');
const fs = require('fs');
const path = require('path');

// Puerto del frontend (se mantiene separado del backend:3000).
const PORT = Number(process.env.PORT) || 5173;
const HOST = process.env.HOST || '0.0.0.0';
// Carpeta raiz desde donde se sirven archivos estaticos.
const ROOT = __dirname;

// Mapeo basico de extensiones a tipo MIME HTTP.
const MIME_TYPES = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
};

function sendNotFound(res) {
  res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' });
  res.end('404 - Recurso no encontrado');
}

// Normaliza ruta y bloquea intentos de salir de ROOT (path traversal).
function safePath(urlPath) {
  const decoded = decodeURIComponent(urlPath.split('?')[0]);
  const relPath = decoded === '/' ? '/index.html' : decoded;
  const absPath = path.normalize(path.join(ROOT, relPath));
  if (!absPath.startsWith(ROOT)) return null;
  return absPath;
}

const server = http.createServer((req, res) => {
  const absPath = safePath(req.url || '/');
  if (!absPath) {
    sendNotFound(res);
    return;
  }

  fs.stat(absPath, (statErr, stat) => {
    if (statErr) {
      sendNotFound(res);
      return;
    }

    // Si solicitan carpeta, entrega index.html.
    const filePath = stat.isDirectory() ? path.join(absPath, 'index.html') : absPath;
    fs.readFile(filePath, (readErr, content) => {
      if (readErr) {
        sendNotFound(res);
        return;
      }

      const ext = path.extname(filePath).toLowerCase();
      const contentType = MIME_TYPES[ext] || 'application/octet-stream';
      res.writeHead(200, { 'Content-Type': contentType });
      res.end(content);
    });
  });
});

server.listen(PORT, HOST, () => {
  console.log(`CleanHome Admin Frontend en http://localhost:${PORT}`);
});
