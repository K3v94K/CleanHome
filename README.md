# CleanHome

Repositorio del proyecto universitario **CleanHome**, enfocado en la gestion de servicios de limpieza a domicilio.

## Estado actual

En este repositorio, la parte implementada y versionada es el backend REST:

- Backend Node.js + Express + MySQL: [`cleanhome-backend/`](./cleanhome-backend)

La app movil Android y otros componentes de arquitectura del documento pueden estar en repositorios o carpetas separadas.

## Estructura del repositorio

```text
CleanHome/
├─ README.md
└─ cleanhome-backend/
   ├─ README.md
   ├─ src/
   ├─ database/
   ├─ package.json
   └─ .env.example
```

## Documentacion por modulo

- Guia completa del backend: [`cleanhome-backend/README.md`](./cleanhome-backend/README.md)

## Inicio rapido (backend)

1. Ir al modulo backend:
   - `cd cleanhome-backend`
2. Crear archivo de entorno:
   - `copy .env.example .env` (Windows)
3. Configurar credenciales de MySQL en `.env`.
4. Importar base de datos con `database/cleanhome.sql`.
5. Instalar dependencias:
   - `npm.cmd install`
6. Ejecutar servidor:
   - `npm.cmd start`

API base URL: `http://localhost:3000`

## Endpoints principales

- Publicos:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /api/servicios`
- Requieren JWT:
  - `POST /api/solicitudes`
  - `GET /api/solicitudes/mis-solicitudes`
- Admin:
  - `GET /api/admin/solicitudes`
  - `PATCH /api/admin/solicitudes/:id/estado`
  - `PATCH /api/admin/solicitudes/:id/asignar-personal`
  - `GET /api/admin/servicios`
  - `POST /api/admin/servicios`
  - `PUT /api/admin/servicios/:id`
  - `DELETE /api/admin/servicios/:id`

## Nota

Para detalle de payloads, autenticacion y flujo completo de pruebas en Postman, usa el README especifico del backend.
