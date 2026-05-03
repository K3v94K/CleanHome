# CleanHome

Proyecto universitario para gestion de servicios de limpieza a domicilio.

## Estructura

```text
CleanHome/
|- README.md
|- cleanhome-backend/
`- cleanhome-admin-frontend/
```

## Modulos

- `cleanhome-backend/`: API REST (Node.js + Express + MySQL)
- `cleanhome-admin-frontend/`: Panel web administrativo (HTML/CSS/JS)

## Ejecutar el proyecto

1. Inicia backend:
   - `cd cleanhome-backend`
   - `copy .env.example .env`
   - Configura credenciales MySQL en `.env`
   - Importa `database/cleanhome.sql`
   - `npm.cmd install`
   - `npm.cmd start`

2. Inicia frontend admin (en otra terminal):
   - `cd cleanhome-admin-frontend`
   - `npm.cmd start`

## URLs

- API: `http://localhost:3000/api`
- Frontend admin: `http://localhost:5173`

## Documentacion por modulo

- Backend: [`cleanhome-backend/README.md`](./cleanhome-backend/README.md)
- Frontend admin: [`cleanhome-admin-frontend/README.md`](./cleanhome-admin-frontend/README.md)
