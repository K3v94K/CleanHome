# CleanHome Admin Frontend

Frontend web administrativo separado del backend.

## Tecnologias

- HTML
- CSS
- JavaScript
- Servidor estatico en Node.js (`server.js`)

## Requisitos

- Backend ejecutandose en `http://localhost:3000`

## Ejecutar

```bash
npm start
```

Frontend en: `http://localhost:5173`

## Login admin

Usa cuenta con rol Admin.

Credenciales por defecto (si BD fue creada con `cleanhome.sql`):

- Correo: `admin@cleanhome.com`
- Clave: `Admin123!`

## Funciones

- Login admin con JWT
- Lista de solicitudes
- Cambio de estado
- Asignacion de personal
- CRUD de servicios (crear, editar, desactivar)

## Configuracion de API

En la pantalla de login puedes ajustar `API Base`.
Valor recomendado:

- `http://localhost:3000/api`
