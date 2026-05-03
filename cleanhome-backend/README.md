# CleanHome Backend

API REST para CleanHome.

## Importante

Este modulo ya no sirve interfaz web. Solo expone endpoints API.

## Tecnologias

- Node.js
- Express
- MySQL
- mysql2
- dotenv
- bcrypt
- jsonwebtoken
- cors

## Estructura

```text
cleanhome-backend/
|- src/
|  |- config/
|  |- controllers/
|  |- middlewares/
|  |- routes/
|  |- app.js
|  `- server.js
|- database/
|  `- cleanhome.sql
|- .env.example
|- package.json
`- README.md
```

## Configuracion

1. Copia `.env.example` a `.env`.
2. Configura tu base de datos.

```env
PORT=3000
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=
DB_NAME=CleanHomeDB
JWT_SECRET=cleanhome_secret_key
JWT_EXPIRES_IN=8h
```

## Base de datos

```bash
mysql -u root -p < database/cleanhome.sql
```

## Ejecutar

```bash
npm install
npm start
```

Backend en: `http://localhost:3000`

## Credenciales admin por defecto

Si importas el SQL desde cero:

- Correo: `admin@cleanhome.com`
- Clave: `Admin123!`

Si ya tenias base creada y falla el login, actualiza hash:

```sql
UPDATE usuarios
SET password_hash = '$2b$10$gHxBFeX.bbwLIwLxNXEfn.2CfnMoYZSiJvJyy7N4FhjPsRJWfWa06'
WHERE correo = 'admin@cleanhome.com';
```

## Endpoints

### Publicos

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/servicios`
- `GET /api/servicios/:id`

### Cliente (JWT)

- `POST /api/solicitudes`
- `GET /api/solicitudes/mis-solicitudes`
- `GET /api/solicitudes/:id`

### Admin (JWT + rol Admin)

- `GET /api/admin/solicitudes`
- `GET /api/admin/personal`
- `PATCH /api/admin/solicitudes/:id/estado`
- `PATCH /api/admin/solicitudes/:id/asignar-personal`
- `GET /api/admin/servicios`
- `POST /api/admin/servicios`
- `PUT /api/admin/servicios/:id`
- `DELETE /api/admin/servicios/:id`
