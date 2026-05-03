# CleanHome Backend

Backend REST API para la aplicación móvil CleanHome, que administra servicios de limpieza a domicilio.

## Tecnologías

- Node.js
- Express
- MySQL
- mysql2
- dotenv
- bcrypt
- jsonwebtoken
- cors
- nodemon

## Estructura del proyecto

```
cleanhome-backend/
├── src/
│   ├── config/
│   │   └── db.js
│   ├── controllers/
│   │   ├── admin.controller.js
│   │   ├── auth.controller.js
│   │   ├── servicios.controller.js
│   │   └── solicitudes.controller.js
│   ├── middlewares/
│   │   ├── auth.middleware.js
   │   └── role.middleware.js
│   ├── routes/
   │   ├── admin.routes.js
n   │   ├── auth.routes.js
   │   ├── servicios.routes.js
   │   └── solicitudes.routes.js
   ├── app.js
   └── server.js
├── database/
│   └── cleanhome.sql
├── .env.example
├── package.json
└── README.md
```

## Instalación

1. Copia `.env.example` a `.env` y ajusta los valores de la base de datos.
2. Ejecuta:

```bash
cd cleanhome-backend
npm install
```

## Crear la base de datos

1. Asegúrate de tener MySQL en ejecución.
2. Importa `database/cleanhome.sql` en tu servidor MySQL:

```bash
mysql -u root -p < database/cleanhome.sql
```

3. Verifica que exista la base de datos `CleanHomeDB` y las tablas.

## Configuración del `.env`

```env
PORT=3000
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=
DB_NAME=CleanHomeDB
JWT_SECRET=cleanhome_secret_key
JWT_EXPIRES_IN=8h
```

## Ejecutar el servidor

```bash
npm run dev
```

## Endpoints disponibles

### Autenticación

- `POST /api/auth/register`
  - Body ejemplo:
    ```json
    {
      "nombre": "Juan Perez",
      "correo": "juan@example.com",
      "telefono": "7000-0000",
      "direccion": "Col. X, San Salvador",
      "password": "Password123"
    }
    ```

- `POST /api/auth/login`
  - Body ejemplo:
    ```json
    {
      "correo": "admin@cleanhome.com",
      "password": "Admin123!"
    }
    ```

### Servicios

- `GET /api/servicios`
- `GET /api/servicios/:id`

### Solicitudes del cliente

- `POST /api/solicitudes`
- `GET /api/solicitudes/mis-solicitudes`
- `GET /api/solicitudes/:id`

### Administración

- `GET /api/admin/solicitudes`
- `PATCH /api/admin/solicitudes/:id/estado` (estados permitidos: `Pendiente`, `Confirmada`, `En proceso`, `Completada`, `Cancelada`)
- `PATCH /api/admin/solicitudes/:id/asignar-personal`
- `GET /api/admin/servicios`
- `POST /api/admin/servicios`
- `PUT /api/admin/servicios/:id`
- `DELETE /api/admin/servicios/:id` (desactivación lógica del servicio)

## Flujo recomendado de prueba en Postman

1. Registrar un usuario cliente en `/api/auth/register`.
2. Iniciar sesión en `/api/auth/login` y copiar el token JWT.
3. Consultar servicios en `/api/servicios`.
4. Crear una solicitud en `/api/solicitudes` con el token.
5. Consultar solicitudes propias en `/api/solicitudes/mis-solicitudes`.
6. Iniciar sesión como administrador usando el usuario inicial.
7. Usar endpoints de administración para consultar solicitudes y manejar servicios.

## Nota

Este backend está diseñado para ser consumido por una aplicación Android. No incluye frontend, pagos ni sincronización con SQLite en esta fase.
