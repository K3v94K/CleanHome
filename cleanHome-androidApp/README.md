# CleanHome Android App

Aplicacion Android para clientes de CleanHome, con soporte de panel admin basico.

## Tecnologias

- Kotlin
- Android XML Views
- AppCompat / Material
- RecyclerView
- Retrofit + Gson
- SQLite local (`SQLiteOpenHelper`)
- SharedPreferences para sesion

## Configuracion de API

La URL base del backend se centraliza en:

```text
app/src/main/java/com/example/proyectopdm2026_gt01_grupo01_limpieza/api/ApiConfig.kt
```

Para emulador Android:

```kotlin
const val BASE_URL = "http://10.0.2.2:3000/api/"
```

Para celular fisico en la misma red Wi-Fi:

```kotlin
const val BASE_URL = "http://IP_DE_TU_PC:3000/api/"
```

No subas tu IP personal al repositorio. Si pruebas en celular fisico, cambia esta URL solo en tu entorno local.

## Permisos

El manifest incluye:

- `INTERNET`
- `ACCESS_NETWORK_STATE`
- `usesCleartextTraffic="true"` para permitir HTTP local durante desarrollo.

## Flujo Cliente

1. Registro de cliente contra `POST /api/auth/register`.
2. Login contra `POST /api/auth/login`.
3. Guardado de JWT y usuario en `SessionManager`.
4. Carga de catalogo desde `GET /api/servicios`.
5. Cache local de servicios en SQLite.
6. Seleccion de servicio y agenda con selector de fecha/hora.
7. Creacion de solicitud contra `POST /api/solicitudes`.
8. Historial desde `GET /api/solicitudes/mis-solicitudes`.
9. Vista de perfil con cierre de sesion.

## Flujo Admin Android

Si el usuario autenticado tiene rol `Admin`, la app abre `AdminActivity`.

Funciones disponibles:

- Ver solicitudes.
- Cambiar estado de solicitud.
- Ver servicios.
- Crear servicio.
- Editar servicio.
- Desactivar servicio.

Funciones no implementadas en Android admin:

- Asignacion de personal de limpieza. Esta funcion sigue disponible en el frontend web.

## SQLite Local

La base local se gestiona en:

```text
app/src/main/java/com/example/proyectopdm2026_gt01_grupo01_limpieza/data/CleanHomeDbHelper.kt
```

Tablas locales principales:

- `usuario_sesion`
- `servicios`
- `solicitudes`
- `solicitudes_pendientes_sync`

El script de referencia esta en:

```text
../cleanhome-backend/database/sqlite_android_cleanhome.sql
```

Ese archivo es para documentar la estructura SQLite local; no se importa en MySQL.

## Validacion Local

1. Levantar MySQL y backend en `http://localhost:3000`.
2. Validar en navegador: `http://localhost:3000/api/servicios`.
3. Levantar frontend web y crear un servicio.
4. Ejecutar Android en emulador con `BASE_URL = "http://10.0.2.2:3000/api/"`.
5. Iniciar sesion o registrar cliente.
6. Confirmar que el servicio aparece en catalogo Android.
7. Crear solicitud desde Android.
8. Ver la solicitud en historial Android y en el panel web/admin.

## Notas de UI

- Los campos de fecha y hora usan selectores nativos.
- El historial muestra fecha corta y hora corta.
- Los estados de solicitud cambian de color:
  - Pendiente: naranja
  - Confirmada: azul
  - En proceso: morado
  - Completada: verde
  - Cancelada: rojo
