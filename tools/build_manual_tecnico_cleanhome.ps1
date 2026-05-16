$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$outDir = Join-Path $root "cdocumentacion"
$workDir = Join-Path $outDir ("docx_build_" + [Guid]::NewGuid().ToString("N"))
$wordDir = Join-Path $workDir "word"
$relsDir = Join-Path $workDir "_rels"
$docRelsDir = Join-Path $wordDir "_rels"
$docxPath = Join-Path $outDir "Manual_Tecnico_CleanHome.docx"

New-Item -ItemType Directory -Force -Path $outDir, $wordDir, $relsDir, $docRelsDir | Out-Null

function Escape-Xml([string]$value) {
  if ($null -eq $value) { return "" }
  return [System.Security.SecurityElement]::Escape($value)
}

function RunXml([string]$text, [bool]$bold = $false, [string]$size = "24", [string]$color = "1F2937") {
  $b = ""
  if ($bold) { $b = "<w:b/>" }
  return "<w:r><w:rPr>$b<w:color w:val=`"$color`"/><w:sz w:val=`"$size`"/></w:rPr><w:t xml:space=`"preserve`">$(Escape-Xml $text)</w:t></w:r>"
}

function Paragraph([string]$text, [string]$style = "Normal", [string]$before = "0", [string]$after = "120", [bool]$bold = $false) {
  return "<w:p><w:pPr><w:pStyle w:val=`"$style`"/><w:spacing w:before=`"$before`" w:after=`"$after`"/></w:pPr>$(RunXml $text $bold)</w:p>"
}

function Bullet([string]$text) {
  return "<w:p><w:pPr><w:pStyle w:val=`"ListParagraph`"/><w:numPr><w:ilvl w:val=`"0`"/><w:numId w:val=`"1`"/></w:numPr><w:spacing w:after=`"120`"/></w:pPr>$(RunXml $text)</w:p>"
}

function CodePara([string]$text) {
  return "<w:p><w:pPr><w:pStyle w:val=`"CodeBlock`"/><w:spacing w:before=`"40`" w:after=`"80`"/></w:pPr><w:r><w:rPr><w:rFonts w:ascii=`"Consolas`" w:hAnsi=`"Consolas`"/><w:sz w:val=`"19`"/><w:color w:val=`"374151`"/></w:rPr><w:t xml:space=`"preserve`">$(Escape-Xml $text)</w:t></w:r></w:p>"
}

function Cell([string]$text, [string]$width, [bool]$header = $false) {
  $fill = "FFFFFF"
  $bold = $false
  if ($header) {
    $fill = "E8EEF7"
    $bold = $true
  }
  return "<w:tc><w:tcPr><w:tcW w:w=`"$width`" w:type=`"dxa`"/><w:shd w:fill=`"$fill`"/><w:tcMar><w:top w:w=`"100`" w:type=`"dxa`"/><w:left w:w=`"120`" w:type=`"dxa`"/><w:bottom w:w=`"100`" w:type=`"dxa`"/><w:right w:w=`"120`" w:type=`"dxa`"/></w:tcMar></w:tcPr><w:p><w:pPr><w:spacing w:after=`"0`"/></w:pPr>$(RunXml $text $bold "21")</w:p></w:tc>"
}

function Table2($rows) {
  $xml = "<w:tbl><w:tblPr><w:tblStyle w:val=`"TableGrid`"/><w:tblW w:w=`"9360`" w:type=`"dxa`"/><w:tblLayout w:type=`"fixed`"/></w:tblPr><w:tblGrid><w:gridCol w:w=`"2800`"/><w:gridCol w:w=`"6560`"/></w:tblGrid>"
  for ($i = 0; $i -lt $rows.Count; $i++) {
    $isHeader = ($i -eq 0)
    $xml += "<w:tr>" + (Cell $rows[$i][0] "2800" $isHeader) + (Cell $rows[$i][1] "6560" $isHeader) + "</w:tr>"
  }
  $xml += "</w:tbl>"
  return $xml
}

function Table3($rows) {
  $xml = "<w:tbl><w:tblPr><w:tblStyle w:val=`"TableGrid`"/><w:tblW w:w=`"9360`" w:type=`"dxa`"/><w:tblLayout w:type=`"fixed`"/></w:tblPr><w:tblGrid><w:gridCol w:w=`"2400`"/><w:gridCol w:w=`"2800`"/><w:gridCol w:w=`"4160`"/></w:tblGrid>"
  for ($i = 0; $i -lt $rows.Count; $i++) {
    $isHeader = ($i -eq 0)
    $xml += "<w:tr>" + (Cell $rows[$i][0] "2400" $isHeader) + (Cell $rows[$i][1] "2800" $isHeader) + (Cell $rows[$i][2] "4160" $isHeader) + "</w:tr>"
  }
  $xml += "</w:tbl>"
  return $xml
}

$body = @()
$body += Paragraph "Manual Tecnico CleanHome" "Title" "0" "160" $true
$body += Paragraph "Backend REST API, base de datos MySQL y soporte SQLite para aplicacion Android" "Subtitle" "0" "240"
$body += Table2 @(
  @("Elemento", "Detalle"),
  @("Proyecto", "CleanHome"),
  @("Rama revisada", "TEST"),
  @("Fecha de referencia", "16 de mayo de 2026"),
  @("Backend", "Node.js, Express, MySQL, JWT"),
  @("Base local Android", "SQLite con soporte de cache, consulta offline y sincronizacion")
)

$body += Paragraph "1. Proposito del documento" "Heading1" "240" "120" $true
$body += Paragraph "Este manual tecnico describe el backend CleanHome en la rama TEST y los ajustes incorporados para que una futura aplicacion Android pueda usar SQLite como almacenamiento local. El backend se mantiene como fuente central de datos, mientras que SQLite funcionara como cache local y cola de sincronizacion."

$body += Paragraph "2. Alcance" "Heading1" "240" "120" $true
$body += Bullet "Documentar tecnologias, estructura de carpetas y configuracion del backend."
$body += Bullet "Describir el modelo de datos principal en MySQL."
$body += Bullet "Describir el esquema SQLite propuesto para Android."
$body += Bullet "Explicar endpoints publicos, de cliente, de administracion y de sincronizacion."
$body += Bullet "Servir como referencia para instalacion, mantenimiento y pruebas tecnicas."

$body += Paragraph "3. Arquitectura general" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Capa", "Componente", "Responsabilidad"),
  @("Cliente movil", "Android + SQLite", "Consumir API, guardar datos locales, permitir consulta offline y enviar solicitudes pendientes."),
  @("API backend", "Node.js + Express", "Exponer endpoints REST, validar JWT, aplicar reglas de negocio y persistir datos centrales."),
  @("Base central", "MySQL CleanHomeDB", "Guardar usuarios, roles, servicios, personal y solicitudes oficiales."),
  @("Panel admin", "Frontend admin separado", "Gestionar servicios, solicitudes, estados y asignacion de personal.")
)

$body += Paragraph "4. Tecnologias principales" "Heading1" "240" "120" $true
$body += Table2 @(
  @("Tecnologia", "Uso"),
  @("Node.js", "Ejecucion del backend."),
  @("Express", "Definicion de rutas HTTP y middlewares."),
  @("mysql2", "Conexion y consultas a MySQL."),
  @("bcrypt", "Hash de contrasenas."),
  @("jsonwebtoken", "Generacion y validacion de JWT."),
  @("cors", "Permitir consumo desde frontend web y app movil."),
  @("SQLite", "Base local de la aplicacion Android.")
)

$body += Paragraph "5. Estructura del backend" "Heading1" "240" "120" $true
$body += CodePara "cleanhome-backend/"
$body += CodePara "|- src/"
$body += CodePara "|  |- config/db.js"
$body += CodePara "|  |- controllers/"
$body += CodePara "|  |- middlewares/"
$body += CodePara "|  |- routes/"
$body += CodePara "|  |- app.js"
$body += CodePara "|  `- server.js"
$body += CodePara "|- database/"
$body += CodePara "|  |- cleanhome.sql"
$body += CodePara "|  |- sqlite_android_cleanhome.sql"
$body += CodePara "|  `- migrations/001_sync_support.sql"
$body += CodePara "`- package.json"

$body += Paragraph "5.1 Estructura completa del proyecto" "Heading1" "240" "120" $true
$body += Paragraph "La estructura actual separa el backend API, el frontend administrativo, scripts de base de datos y documentacion tecnica. Los archivos generados por herramientas o dependencias, como node_modules, package-lock.json y carpetas temporales de render, no forman parte de la logica principal."
$body += CodePara "CleanHome/"
$body += CodePara "|- README.md"
$body += CodePara "|- cleanhome-backend/"
$body += CodePara "|  |- README.md"
$body += CodePara "|  |- package.json"
$body += CodePara "|  |- package-lock.json"
$body += CodePara "|  |- database/"
$body += CodePara "|  |  |- cleanhome.sql"
$body += CodePara "|  |  |- sqlite_android_cleanhome.sql"
$body += CodePara "|  |  `- migrations/"
$body += CodePara "|  |     `- 001_sync_support.sql"
$body += CodePara "|  `- src/"
$body += CodePara "|     |- app.js"
$body += CodePara "|     |- server.js"
$body += CodePara "|     |- config/db.js"
$body += CodePara "|     |- controllers/"
$body += CodePara "|     |- middlewares/"
$body += CodePara "|     `- routes/"
$body += CodePara "|- cleanhome-admin-frontend/"
$body += CodePara "|  |- README.md"
$body += CodePara "|  |- package.json"
$body += CodePara "|  |- server.js"
$body += CodePara "|  |- index.html"
$body += CodePara "|  |- app.js"
$body += CodePara "|  `- styles.css"
$body += CodePara "|- documentacion/"
$body += CodePara "|  `- Documentacion_Tecnica_CleanHome.pdf"
$body += CodePara "|- cdocumentacion/"
$body += CodePara "|  `- Manual_Tecnico_CleanHome.docx"
$body += CodePara "`- tools/"
$body += CodePara "   `- build_manual_tecnico_cleanhome.ps1"

$body += Paragraph "5.2 Descripcion archivo por archivo: raiz" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Archivo o carpeta", "Tipo", "Funcion tecnica"),
  @("README.md", "Documentacion", "Explica la estructura general del proyecto, modulos disponibles, comandos de ejecucion y URLs principales."),
  @("cleanhome-backend/", "Modulo backend", "Contiene la API REST, conexion a base de datos, controladores, rutas, middlewares y scripts SQL."),
  @("cleanhome-admin-frontend/", "Modulo frontend", "Contiene el panel web administrativo separado del backend."),
  @("documentacion/", "Documentacion existente", "Carpeta con documentacion previa en PDF."),
  @("cdocumentacion/", "Documentacion generada", "Carpeta destino solicitada para el manual tecnico en Word."),
  @("tools/", "Utilidades internas", "Contiene scripts auxiliares para generar documentos o automatizar tareas del proyecto.")
)

$body += Paragraph "5.3 Descripcion archivo por archivo: backend" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Archivo", "Responsabilidad", "Detalle"),
  @("cleanhome-backend/package.json", "Configuracion Node.js", "Define nombre del paquete, scripts npm y dependencias del backend: Express, mysql2, dotenv, bcrypt, jsonwebtoken y cors."),
  @("cleanhome-backend/package-lock.json", "Bloqueo de dependencias", "Registra versiones exactas instaladas para que npm pueda reproducir el arbol de dependencias."),
  @("cleanhome-backend/README.md", "Guia tecnica del backend", "Describe instalacion, configuracion, base de datos, endpoints y soporte SQLite Android."),
  @("cleanhome-backend/src/server.js", "Punto de arranque", "Carga variables de entorno con dotenv, importa app.js y levanta el servidor HTTP en el puerto configurado."),
  @("cleanhome-backend/src/app.js", "Configuracion Express", "Crea la app Express, habilita CORS, JSON, monta rutas /api/auth, /api/servicios, /api/solicitudes y /api/admin, y define manejadores de error."),
  @("cleanhome-backend/src/config/db.js", "Conexion MySQL", "Crea un pool mysql2/promise usando DB_HOST, DB_USER, DB_PASSWORD y DB_NAME. Es reutilizado por los controladores."),
  @("cleanhome-backend/src/controllers/auth.controller.js", "Autenticacion", "Gestiona registro de clientes, hash de contrasena con bcrypt, validacion de credenciales y generacion de JWT."),
  @("cleanhome-backend/src/controllers/servicios.controller.js", "Servicios publicos", "Lista servicios activos, consulta detalle por id y soporta updated_since para sincronizacion incremental Android."),
  @("cleanhome-backend/src/controllers/solicitudes.controller.js", "Solicitudes cliente", "Crea solicitudes, lista solicitudes propias, consulta por id validando propietario y sincroniza solicitudes offline con client_temp_id."),
  @("cleanhome-backend/src/controllers/admin.controller.js", "Gestion administrativa", "Lista solicitudes, personal y servicios; actualiza estado, asigna personal, crea/edita/desactiva servicios y refresca updated_at."),
  @("cleanhome-backend/src/middlewares/auth.middleware.js", "Autenticacion JWT", "Valida encabezado Authorization: Bearer token, verifica firma y expiracion, y coloca payload en req.user."),
  @("cleanhome-backend/src/middlewares/role.middleware.js", "Autorizacion por rol", "Permite restringir rutas a un rol especifico, actualmente usado para Admin."),
  @("cleanhome-backend/src/routes/auth.routes.js", "Rutas auth", "Expone POST /register y POST /login."),
  @("cleanhome-backend/src/routes/servicios.routes.js", "Rutas servicios", "Expone GET / y GET /:id para catalogo publico de servicios."),
  @("cleanhome-backend/src/routes/solicitudes.routes.js", "Rutas solicitudes", "Expone creacion, historial, detalle y sincronizacion de solicitudes para usuarios autenticados."),
  @("cleanhome-backend/src/routes/admin.routes.js", "Rutas admin", "Aplica authMiddleware y requireRole('Admin') a toda la gestion administrativa.")
)

$body += Paragraph "5.4 Descripcion archivo por archivo: base de datos" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Archivo", "Motor", "Funcion tecnica"),
  @("database/cleanhome.sql", "MySQL", "Crea CleanHomeDB, tablas principales, relaciones, semillas de roles, servicios, personal y usuario administrador."),
  @("database/migrations/001_sync_support.sql", "MySQL", "Agrega soporte de sincronizacion a bases existentes: updated_at, fecha_creacion y client_temp_id."),
  @("database/sqlite_android_cleanhome.sql", "SQLite", "Define la base local Android para cache, usuario de sesion, solicitudes offline, cola de sincronizacion e indices.")
)

$body += Paragraph "5.5 Descripcion archivo por archivo: frontend administrativo" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Archivo", "Responsabilidad", "Detalle"),
  @("cleanhome-admin-frontend/package.json", "Configuracion frontend", "Define scripts npm start/dev para ejecutar el servidor estatico."),
  @("cleanhome-admin-frontend/README.md", "Guia del panel admin", "Documenta requisitos, URL, credenciales y funciones disponibles."),
  @("cleanhome-admin-frontend/server.js", "Servidor estatico", "Sirve index.html, CSS y JS en el puerto 5173, con control basico de tipos MIME y proteccion contra path traversal."),
  @("cleanhome-admin-frontend/index.html", "Estructura visual", "Define la interfaz HTML del panel administrativo: login, vistas, formularios y secciones."),
  @("cleanhome-admin-frontend/app.js", "Logica del panel", "Consume la API, maneja token JWT, carga solicitudes, personal y servicios, y ejecuta acciones administrativas."),
  @("cleanhome-admin-frontend/styles.css", "Estilos", "Define apariencia, layout, tablas, formularios, botones y estados visuales del panel.")
)

$body += Paragraph "5.6 Descripcion archivo por archivo: documentacion y herramientas" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Archivo", "Uso", "Detalle"),
  @("documentacion/Documentacion_Tecnica_CleanHome.pdf", "Documento PDF", "Documentacion previa no modificada durante esta generacion."),
  @("cdocumentacion/Manual_Tecnico_CleanHome.docx", "Entregable Word", "Manual tecnico generado para describir arquitectura, estructura, base de datos, endpoints y sincronizacion."),
  @("tools/build_manual_tecnico_cleanhome.ps1", "Script generador", "Construye el archivo Word como paquete OOXML para poder regenerar el manual tecnico de forma reproducible.")
)

$body += Paragraph "6. Configuracion del entorno" "Heading1" "240" "120" $true
$body += Paragraph "El backend utiliza variables de entorno en un archivo .env. Los valores base esperados son:"
$body += CodePara "PORT=3000"
$body += CodePara "DB_HOST=localhost"
$body += CodePara "DB_USER=root"
$body += CodePara "DB_PASSWORD="
$body += CodePara "DB_NAME=CleanHomeDB"
$body += CodePara "JWT_SECRET=cleanhome_secret_key"
$body += CodePara "JWT_EXPIRES_IN=8h"
$body += Paragraph "Para ejecutar el backend:"
$body += CodePara "npm install"
$body += CodePara "npm start"
$body += Paragraph "La API queda disponible por defecto en http://localhost:3000."

$body += Paragraph "7. Base de datos MySQL" "Heading1" "240" "120" $true
$body += Paragraph "El archivo principal de base de datos es database/cleanhome.sql. Crea la base CleanHomeDB y las tablas centrales del sistema."
$body += Table3 @(
  @("Tabla", "Campos clave", "Descripcion"),
  @("roles", "id_rol, nombre_rol", "Define roles Admin y Cliente."),
  @("usuarios", "id_usuario, correo, password_hash, id_rol, fecha_creacion, updated_at", "Usuarios registrados y administradores."),
  @("servicios", "id_servicio, precio, activo, updated_at", "Catalogo central de servicios."),
  @("personal_limpieza", "id_personal, disponible, updated_at", "Personal asignable a solicitudes."),
  @("solicitudes", "id_solicitud, id_usuario, id_servicio, estado, client_temp_id, updated_at", "Solicitudes de limpieza creadas por clientes.")
)
$body += Paragraph "Para una instalacion nueva se ejecuta:"
$body += CodePara "mysql -u root -p < database/cleanhome.sql"
$body += Paragraph "Para una base ya existente se aplica la migracion de sincronizacion una sola vez:"
$body += CodePara "mysql -u root -p < database/migrations/001_sync_support.sql"

$body += Paragraph "8. Soporte de sincronizacion" "Heading1" "240" "120" $true
$body += Paragraph "Los campos updated_at permiten que Android consulte solo registros modificados desde la ultima sincronizacion. El campo client_temp_id permite que solicitudes creadas offline se envien al backend sin duplicarse si la app reintenta la operacion."
$body += Bullet "updated_at: marca cada cambio relevante para sincronizacion incremental."
$body += Bullet "client_temp_id: identificador generado por Android para solicitudes locales."
$body += Bullet "sync_metadata: tabla local SQLite para recordar la ultima sincronizacion por modulo."
$body += Bullet "solicitudes_pendientes_sync: cola local de solicitudes que aun deben enviarse al backend."

$body += Paragraph "9. Base local SQLite para Android" "Heading1" "240" "120" $true
$body += Paragraph "El script database/sqlite_android_cleanhome.sql define la base local que usara Android. No sustituye MySQL; funciona como almacenamiento local del dispositivo."
$body += Table3 @(
  @("Tabla SQLite", "Origen o uso", "Funcion"),
  @("usuario_sesion", "Login API", "Guardar datos basicos del usuario autenticado y token."),
  @("servicios", "GET /api/servicios", "Cache local del catalogo de servicios."),
  @("personal_limpieza", "Admin/API futura", "Cache local de personal si la app lo requiere."),
  @("solicitudes", "GET /api/solicitudes/mis-solicitudes", "Historial local visible offline."),
  @("solicitudes_pendientes_sync", "Creacion offline", "Cola de solicitudes pendientes de enviar."),
  @("sync_metadata", "Proceso local", "Fechas de ultima sincronizacion por modulo.")
)

$body += Paragraph "10. Endpoints publicos" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Metodo", "Ruta", "Uso"),
  @("POST", "/api/auth/register", "Registrar usuarios cliente."),
  @("POST", "/api/auth/login", "Autenticar usuario y devolver JWT."),
  @("GET", "/api/servicios", "Listar servicios activos."),
  @("GET", "/api/servicios/:id", "Consultar detalle de servicio activo.")
)

$body += Paragraph "11. Endpoints de cliente" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Metodo", "Ruta", "Uso"),
  @("POST", "/api/solicitudes", "Crear solicitud para el usuario autenticado."),
  @("GET", "/api/solicitudes/mis-solicitudes", "Listar solicitudes del usuario autenticado."),
  @("GET", "/api/solicitudes/mis-solicitudes?updated_since=fecha", "Listar solicitudes modificadas desde una fecha."),
  @("POST", "/api/solicitudes/sync", "Enviar solicitudes creadas offline desde Android."),
  @("GET", "/api/solicitudes/:id", "Consultar solicitud puntual validando propietario.")
)

$body += Paragraph "12. Endpoints administrativos" "Heading1" "240" "120" $true
$body += Table3 @(
  @("Metodo", "Ruta", "Uso"),
  @("GET", "/api/admin/solicitudes", "Listar todas las solicitudes."),
  @("GET", "/api/admin/personal", "Listar personal de limpieza."),
  @("PATCH", "/api/admin/solicitudes/:id/estado", "Actualizar estado de solicitud."),
  @("PATCH", "/api/admin/solicitudes/:id/asignar-personal", "Asignar personal a solicitud."),
  @("GET", "/api/admin/servicios", "Listar servicios activos e inactivos."),
  @("POST", "/api/admin/servicios", "Crear servicio."),
  @("PUT", "/api/admin/servicios/:id", "Actualizar servicio."),
  @("DELETE", "/api/admin/servicios/:id", "Desactivar servicio logicamente.")
)

$body += Paragraph "13. Flujo recomendado Android + SQLite" "Heading1" "240" "120" $true
$body += Bullet "Al iniciar sesion, Android guarda datos del usuario y token en usuario_sesion."
$body += Bullet "Android descarga servicios y solicitudes, los guarda en SQLite y actualiza sync_metadata."
$body += Bullet "Si no hay internet, la app consulta datos desde SQLite."
$body += Bullet "Si el usuario crea una solicitud offline, Android genera client_temp_id y la guarda en solicitudes_pendientes_sync."
$body += Bullet "Cuando vuelve la conexion, Android envia la cola a POST /api/solicitudes/sync."
$body += Bullet "Despues de sincronizar, Android consulta cambios con updated_since para refrescar su cache local."

$body += Paragraph "14. Ejemplo de payload de sincronizacion" "Heading1" "240" "120" $true
$body += CodePara "{"
$body += CodePara '  "solicitudes": ['
$body += CodePara "    {"
$body += CodePara '      "client_temp_id": "android-uuid-001",'
$body += CodePara '      "id_servicio": 1,'
$body += CodePara '      "fecha_servicio": "2026-05-20",'
$body += CodePara '      "hora_servicio": "09:00:00",'
$body += CodePara '      "direccion_atencion": "San Salvador"'
$body += CodePara "    }"
$body += CodePara "  ]"
$body += CodePara "}"

$body += Paragraph "15. Seguridad y validaciones" "Heading1" "240" "120" $true
$body += Bullet "Las rutas de cliente y administracion requieren JWT."
$body += Bullet "Las rutas admin requieren rol Admin mediante middleware."
$body += Bullet "Las contrasenas se almacenan como hash con bcrypt."
$body += Bullet "Las solicitudes del cliente se filtran por req.user.id_usuario."
$body += Bullet "Los estados administrativos se validan contra una lista permitida."
$body += Bullet "updated_since se valida como fecha antes de consultar datos incrementales."

$body += Paragraph "16. Credenciales de administracion inicial" "Heading1" "240" "120" $true
$body += Table2 @(
  @("Campo", "Valor"),
  @("Correo", "admin@cleanhome.com"),
  @("Clave", "Admin123!")
)

$body += Paragraph "17. Consideraciones de mantenimiento" "Heading1" "240" "120" $true
$body += Bullet "Mantener sincronizados los cambios de esquema entre cleanhome.sql, migraciones y sqlite_android_cleanhome.sql."
$body += Bullet "Agregar migraciones nuevas cuando una base MySQL ya existente requiera cambios."
$body += Bullet "No guardar contrasenas en SQLite; guardar solo token si la politica de seguridad del proyecto lo permite."
$body += Bullet "Definir expiracion o renovacion de token para la app Android."
$body += Bullet "Probar escenarios offline: creacion local, reintento, duplicado por client_temp_id y actualizacion incremental."

$body += Paragraph "18. Archivos tecnicos relacionados" "Heading1" "240" "120" $true
$body += Table2 @(
  @("Archivo", "Descripcion"),
  @("cleanhome-backend/database/cleanhome.sql", "Esquema MySQL principal."),
  @("cleanhome-backend/database/migrations/001_sync_support.sql", "Migracion para soporte de sincronizacion."),
  @("cleanhome-backend/database/sqlite_android_cleanhome.sql", "Esquema SQLite local para Android."),
  @("cleanhome-backend/src/controllers/solicitudes.controller.js", "Logica de solicitudes y sincronizacion."),
  @("cleanhome-backend/src/controllers/servicios.controller.js", "Consulta de servicios con updated_since."),
  @("cleanhome-backend/src/controllers/admin.controller.js", "Gestion administrativa y actualizacion de updated_at."),
  @("cleanhome-backend/src/routes/solicitudes.routes.js", "Rutas de solicitudes y sync.")
)

$body += "<w:sectPr><w:pgSz w:w=`"12240`" w:h=`"15840`"/><w:pgMar w:top=`"1440`" w:right=`"1440`" w:bottom=`"1440`" w:left=`"1440`" w:header=`"720`" w:footer=`"720`" w:gutter=`"0`"/></w:sectPr>"

$documentXml = "<?xml version=`"1.0`" encoding=`"UTF-8`" standalone=`"yes`"?><w:document xmlns:wpc=`"http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas`" xmlns:mc=`"http://schemas.openxmlformats.org/markup-compatibility/2006`" xmlns:o=`"urn:schemas-microsoft-com:office:office`" xmlns:r=`"http://schemas.openxmlformats.org/officeDocument/2006/relationships`" xmlns:m=`"http://schemas.openxmlformats.org/officeDocument/2006/math`" xmlns:v=`"urn:schemas-microsoft-com:vml`" xmlns:wp14=`"http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing`" xmlns:wp=`"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing`" xmlns:w10=`"urn:schemas-microsoft-com:office:word`" xmlns:w=`"http://schemas.openxmlformats.org/wordprocessingml/2006/main`" xmlns:w14=`"http://schemas.microsoft.com/office/word/2010/wordml`" xmlns:wpg=`"http://schemas.microsoft.com/office/word/2010/wordprocessingGroup`" xmlns:wpi=`"http://schemas.microsoft.com/office/word/2010/wordprocessingInk`" xmlns:wne=`"http://schemas.microsoft.com/office/word/2006/wordml`" xmlns:wps=`"http://schemas.microsoft.com/office/word/2010/wordprocessingShape`" mc:Ignorable=`"w14 wp14`"><w:body>$($body -join '')</w:body></w:document>"

$stylesXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults>
    <w:rPrDefault><w:rPr><w:rFonts w:ascii="Arial" w:hAnsi="Arial"/><w:sz w:val="24"/><w:color w:val="1F2937"/></w:rPr></w:rPrDefault>
    <w:pPrDefault><w:pPr><w:spacing w:line="259" w:lineRule="auto" w:after="120"/></w:pPr></w:pPrDefault>
  </w:docDefaults>
  <w:style w:type="paragraph" w:default="1" w:styleId="Normal"><w:name w:val="Normal"/><w:qFormat/></w:style>
  <w:style w:type="paragraph" w:styleId="Title"><w:name w:val="Title"/><w:basedOn w:val="Normal"/><w:next w:val="Subtitle"/><w:qFormat/><w:pPr><w:spacing w:after="160"/></w:pPr><w:rPr><w:b/><w:sz w:val="44"/><w:color w:val="12355B"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Subtitle"><w:name w:val="Subtitle"/><w:basedOn w:val="Normal"/><w:qFormat/><w:rPr><w:sz w:val="24"/><w:color w:val="4B5563"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="heading 1"/><w:basedOn w:val="Normal"/><w:next w:val="Normal"/><w:qFormat/><w:pPr><w:outlineLvl w:val="0"/><w:spacing w:before="240" w:after="120"/></w:pPr><w:rPr><w:b/><w:sz w:val="32"/><w:color w:val="12355B"/></w:rPr></w:style>
  <w:style w:type="paragraph" w:styleId="ListParagraph"><w:name w:val="List Paragraph"/><w:basedOn w:val="Normal"/><w:qFormat/><w:pPr><w:ind w:left="720" w:hanging="360"/></w:pPr></w:style>
  <w:style w:type="paragraph" w:styleId="CodeBlock"><w:name w:val="Code Block"/><w:basedOn w:val="Normal"/><w:pPr><w:ind w:left="240"/><w:spacing w:after="80"/></w:pPr><w:rPr><w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/><w:sz w:val="19"/><w:color w:val="374151"/></w:rPr></w:style>
  <w:style w:type="table" w:styleId="TableGrid"><w:name w:val="Table Grid"/><w:tblPr><w:tblBorders><w:top w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/><w:left w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/><w:bottom w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/><w:right w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/><w:insideH w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/><w:insideV w:val="single" w:sz="4" w:space="0" w:color="CBD5E1"/></w:tblBorders></w:tblPr></w:style>
</w:styles>
'@

$numberingXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:numbering xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:abstractNum w:abstractNumId="0">
    <w:multiLevelType w:val="hybridMultilevel"/>
    <w:lvl w:ilvl="0">
      <w:start w:val="1"/>
      <w:numFmt w:val="bullet"/>
      <w:lvlText w:val="•"/>
      <w:lvlJc w:val="left"/>
      <w:pPr><w:ind w:left="720" w:hanging="360"/></w:pPr>
      <w:rPr><w:rFonts w:ascii="Symbol" w:hAnsi="Symbol" w:hint="default"/></w:rPr>
    </w:lvl>
  </w:abstractNum>
  <w:num w:numId="1"><w:abstractNumId w:val="0"/></w:num>
</w:numbering>
'@

$settingsXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:settings xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:defaultTabStop w:val="720"/>
  <w:compat/>
</w:settings>
'@

$contentTypesXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
  <Override PartName="/word/numbering.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml"/>
  <Override PartName="/word/settings.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml"/>
</Types>
'@

$rootRelsXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>
'@

$docRelsXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/numbering" Target="numbering.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/settings" Target="settings.xml"/>
</Relationships>
'@

[System.IO.File]::WriteAllText((Join-Path $workDir "[Content_Types].xml"), $contentTypesXml, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $relsDir ".rels"), $rootRelsXml, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $wordDir "document.xml"), $documentXml, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $wordDir "styles.xml"), $stylesXml, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $wordDir "numbering.xml"), $numberingXml, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $wordDir "settings.xml"), $settingsXml, [System.Text.Encoding]::UTF8)
[System.IO.File]::WriteAllText((Join-Path $docRelsDir "document.xml.rels"), $docRelsXml, [System.Text.Encoding]::UTF8)

if (Test-Path $docxPath) {
  [System.IO.File]::Delete($docxPath)
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::CreateFromDirectory($workDir, $docxPath)

Write-Output $docxPath
