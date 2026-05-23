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

Si no hay datos relevantes, se recomienda recrear la base desde cero usando `database/cleanhome.sql`.

```bash
mysql -u root -p < database/cleanhome.sql
```

Si ya tenias la base creada antes de estos cambios, aplica la migracion una sola vez:

```bash
mysql -u root -p < database/migrations/001_sync_support.sql
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
- `GET /api/solicitudes/mis-solicitudes?updated_since=2026-05-16T10:00:00`
- `POST /api/solicitudes/sync`
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

## Soporte para SQLite en Android

El backend se mantiene como fuente central de datos. La app Android usa Retrofit para consumir la API y SQLite como almacenamiento local de sesion, servicios y solicitudes.

Para emulador Android, la URL del backend local se configura como:

```text
http://10.0.2.2:3000/api/
```

Para celular fisico en la misma red:

```text
http://IP_DE_TU_PC:3000/api/
```

Endpoints usados por Android:

- `POST /api/auth/register`: registro de clientes.
- `POST /api/auth/login`: autenticacion y obtencion de JWT.
- `GET /api/servicios`: catalogo de servicios.
- `POST /api/solicitudes`: agendar solicitud autenticada.
- `GET /api/solicitudes/mis-solicitudes`: historial del cliente.
- `GET /api/admin/solicitudes`: listado admin en Android.
- `PATCH /api/admin/solicitudes/:id/estado`: cambio de estado admin.
- `GET /api/admin/servicios`: listado admin de servicios.
- `POST /api/admin/servicios`: crear servicio desde Android admin.
- `PUT /api/admin/servicios/:id`: editar servicio desde Android admin.
- `DELETE /api/admin/servicios/:id`: desactivar servicio desde Android admin.

Soporte de sincronizacion preparado:

- `GET /api/servicios?updated_since=fecha`: obtiene servicios modificados desde una fecha. Si se usa este parametro, tambien puede devolver servicios desactivados para que Android actualice su cache local.
- `GET /api/solicitudes/mis-solicitudes?updated_since=fecha`: obtiene solicitudes del usuario modificadas desde una fecha.
- `POST /api/solicitudes/sync`: recibe solicitudes creadas offline en Android.

Ejemplo de sincronizacion de solicitudes:

```json
{
  "solicitudes": [
    {
      "client_temp_id": "android-uuid-001",
      "id_servicio": 1,
      "fecha_servicio": "2026-05-20",
      "hora_servicio": "09:00:00",
      "direccion_atencion": "San Salvador"
    }
  ]
}
```

`client_temp_id` debe ser generado por Android y guardado en SQLite. Si la app reintenta la sincronizacion, el backend lo usa para no crear solicitudes duplicadas.
