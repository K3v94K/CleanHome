# CleanHome

Proyecto universitario para gestion de servicios de limpieza a domicilio.

## Estructura

```text
CleanHome/
|- README.md
|- cleanhome-backend/
|- cleanhome-admin-frontend/
`- cleanHome-androidApp/
```

## Modulos

- `cleanhome-backend/`: API REST (Node.js + Express + MySQL)
- `cleanhome-admin-frontend/`: Panel web administrativo (HTML/CSS/JS)
- `cleanHome-androidApp/`: Aplicacion Android cliente/admin (Kotlin + XML + Retrofit + SQLite)

## Ejecutar el proyecto

1. Prepara la base de datos:
   - Usa MySQL instalado localmente.
   - Si no hay datos relevantes, elimina y crea la base desde cero.
   - Importa `cleanhome-backend/database/cleanhome.sql`.
   - Si tu base viene de una version anterior, aplica `cleanhome-backend/database/migrations/001_sync_support.sql`.

2. Inicia backend:
   - `cd cleanhome-backend`
   - `copy .env.example .env`
   - Configura credenciales MySQL en `.env`
   - `npm.cmd install`
   - `npm.cmd start`

3. Inicia frontend admin (en otra terminal):
   - `cd cleanhome-admin-frontend`
   - `npm.cmd start`

4. Ejecuta Android desde Android Studio:
   - Abre `cleanHome-androidApp/`
   - Para emulador Android usa `http://10.0.2.2:3000/api/`
   - Para celular fisico usa `http://IP_DE_TU_PC:3000/api/`

## URLs

- API: `http://localhost:3000/api`
- Frontend admin: `http://localhost:5173`
- Android emulador hacia backend local: `http://10.0.2.2:3000/api/`

## Flujo de validacion

1. Crear o editar un servicio desde el frontend web.
2. Confirmar que `GET http://localhost:3000/api/servicios` devuelve el servicio.
3. Abrir la app Android.
4. Registrar o iniciar sesion con un usuario cliente.
5. Verificar que el catalogo Android muestra el servicio.
6. Agendar una solicitud desde Android.
7. Verificar la solicitud en historial Android y en el panel admin web.

## Documentacion por modulo

- Backend: [`cleanhome-backend/README.md`](./cleanhome-backend/README.md)
- Frontend admin: [`cleanhome-admin-frontend/README.md`](./cleanhome-admin-frontend/README.md)
- Android: [`cleanHome-androidApp/README.md`](./cleanHome-androidApp/README.md)
