from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from textwrap import wrap


ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "documentacion" / "Documentacion_Tecnica_CleanHome.pdf"
SOURCE = ROOT / "documentacion" / "Manual_Tecnico_CleanHome.md"

PAGE_W = 612
PAGE_H = 792
M_LEFT = 58
M_RIGHT = 58
M_TOP = 58
M_BOTTOM = 58
LINE_H = 14


@dataclass
class TextBlock:
    text: str
    kind: str = "p"


def esc_pdf(text: str) -> str:
    return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)")


def asciiish(text: str) -> str:
    replacements = {
        "á": "a",
        "é": "e",
        "í": "i",
        "ó": "o",
        "ú": "u",
        "Á": "A",
        "É": "E",
        "Í": "I",
        "Ó": "O",
        "Ú": "U",
        "ñ": "n",
        "Ñ": "N",
        "¿": "",
        "¡": "",
        "–": "-",
        "—": "-",
    }
    for src, dst in replacements.items():
        text = text.replace(src, dst)
    return text


def manual_blocks() -> list[TextBlock]:
    b: list[TextBlock] = []
    add = b.append

    add(TextBlock("Manual Tecnico CleanHome", "title"))
    add(TextBlock("Guia de instalacion, ejecucion, arquitectura e integracion local", "subtitle"))
    add(TextBlock("Fecha de actualizacion: 23 de mayo de 2026", "meta"))
    add(TextBlock("Proyecto: CleanHome | Backend: Node.js + Express + MySQL | Android: Kotlin + Retrofit + SQLite | Web: HTML/CSS/JS", "meta"))

    add(TextBlock("1. Proposito", "h1"))
    add(TextBlock("Este manual tecnico describe como preparar, levantar y validar el proyecto CleanHome de extremo a extremo. Incluye base de datos MySQL, backend REST, aplicativo web administrativo y aplicativo Android con almacenamiento local SQLite."))
    add(TextBlock("El documento tambien resume arquitectura, estructura de carpetas, endpoints, variables de entorno, flujo de pruebas, mantenimiento y consideraciones de seguridad."))

    add(TextBlock("2. Alcance del sistema", "h1"))
    for item in [
        "Backend REST para autenticacion, catalogo de servicios, solicitudes y administracion.",
        "Base central MySQL como fuente oficial de datos.",
        "Frontend web administrativo para gestionar servicios, solicitudes y asignaciones.",
        "Aplicacion Android para clientes y panel admin basico.",
        "SQLite local en Android para cache de servicios, sesion e historial.",
        "Preparacion para despliegue futuro cambiando configuracion de URL y ambiente.",
    ]:
        add(TextBlock(item, "bullet"))

    add(TextBlock("3. Arquitectura general", "h1"))
    add(TextBlock("Android App -> Backend Express -> MySQL", "code"))
    add(TextBlock("Admin Web -> Backend Express -> MySQL", "code"))
    add(TextBlock("El backend es la capa central. Android y el panel web no acceden directamente a MySQL; consumen endpoints HTTP bajo /api. Android usa SQLite solo como almacenamiento local de apoyo."))

    add(TextBlock("4. Requisitos de ambiente", "h1"))
    for item in [
        "Node.js y npm instalados.",
        "MySQL Server instalado y en ejecucion.",
        "Base de datos creada desde cleanhome-backend/database/cleanhome.sql.",
        "Android Studio instalado para compilar y ejecutar cleanHome-androidApp.",
        "Emulador Android o dispositivo fisico en la misma red que la PC.",
        "Git instalado para control de versiones.",
    ]:
        add(TextBlock(item, "bullet"))

    add(TextBlock("5. Estructura del repositorio", "h1"))
    for line in [
        "CleanHome/",
        "|- cleanhome-backend/            API REST Node.js + Express",
        "|- cleanhome-admin-frontend/     Panel web administrativo",
        "|- cleanHome-androidApp/         Aplicacion Android Kotlin/XML",
        "|- cdocumentacion/               Fuente Word del manual tecnico",
        "|- documentacion/                PDF del manual tecnico",
        "|- tools/                        Scripts auxiliares",
        "`- README.md                     Guia general del proyecto",
    ]:
        add(TextBlock(line, "code"))

    add(TextBlock("6. Base de datos MySQL", "h1"))
    add(TextBlock("Si no hay datos relevantes, la opcion recomendada es recrear la base desde cero usando el script principal."))
    for line in [
        "mysql -u TU_USUARIO -p",
        "DROP DATABASE IF EXISTS CleanHomeDB;",
        "CREATE DATABASE CleanHomeDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;",
        "exit",
        "mysql -u TU_USUARIO -p CleanHomeDB < cleanhome-backend/database/cleanhome.sql",
    ]:
        add(TextBlock(line, "code"))
    add(TextBlock("Si la base ya existia y se desea migrar, aplicar una sola vez:"))
    add(TextBlock("mysql -u TU_USUARIO -p CleanHomeDB < cleanhome-backend/database/migrations/001_sync_support.sql", "code"))
    add(TextBlock("Tablas principales: roles, usuarios, servicios, personal_limpieza y solicitudes. La tabla solicitudes incluye client_temp_id para evitar duplicados en sincronizacion movil."))

    add(TextBlock("7. Configuracion del backend", "h1"))
    add(TextBlock("Crear cleanhome-backend/.env. Este archivo no debe subirse a Git."))
    for line in [
        "PORT=3000",
        "DB_HOST=localhost",
        "DB_USER=TU_USUARIO",
        "DB_PASSWORD=TU_PASSWORD",
        "DB_NAME=CleanHomeDB",
        "JWT_SECRET=CAMBIAR_EN_CADA_AMBIENTE",
        "JWT_EXPIRES_IN=8h",
    ]:
        add(TextBlock(line, "code"))
    add(TextBlock("Para levantar el backend:"))
    for line in ["cd cleanhome-backend", "npm install", "npm start"]:
        add(TextBlock(line, "code"))
    add(TextBlock("URL esperada: http://localhost:3000/api"))
    add(TextBlock("Validacion rapida: abrir http://localhost:3000/api/servicios y confirmar una respuesta JSON con servicios."))

    add(TextBlock("8. Endpoints principales", "h1"))
    endpoints = [
        "POST /api/auth/register - Registro de cliente.",
        "POST /api/auth/login - Login y retorno de JWT.",
        "GET /api/servicios - Catalogo publico de servicios activos.",
        "GET /api/servicios/:id - Detalle de servicio.",
        "POST /api/solicitudes - Crear solicitud autenticada.",
        "GET /api/solicitudes/mis-solicitudes - Historial del cliente autenticado.",
        "POST /api/solicitudes/sync - Sincronizacion de solicitudes offline.",
        "GET /api/admin/solicitudes - Solicitudes para admin.",
        "PATCH /api/admin/solicitudes/:id/estado - Cambio de estado.",
        "PATCH /api/admin/solicitudes/:id/asignar-personal - Asignacion de personal.",
        "GET /api/admin/servicios - Lista admin de servicios.",
        "POST /api/admin/servicios - Crear servicio.",
        "PUT /api/admin/servicios/:id - Editar servicio.",
        "DELETE /api/admin/servicios/:id - Desactivar servicio.",
    ]
    for item in endpoints:
        add(TextBlock(item, "bullet"))

    add(TextBlock("9. Aplicativo web administrativo", "h1"))
    add(TextBlock("El panel web se encuentra en cleanhome-admin-frontend y se sirve como frontend estatico mediante Node.js."))
    for line in ["cd cleanhome-admin-frontend", "npm install", "npm start"]:
        add(TextBlock(line, "code"))
    add(TextBlock("URL esperada: http://localhost:5173"))
    for item in [
        "Login con cuenta de rol Admin.",
        "Listado de solicitudes.",
        "Cambio de estado de solicitud.",
        "Asignacion de personal de limpieza.",
        "CRUD logico de servicios: crear, editar y desactivar.",
        "La URL base de API se configura desde la pantalla de login.",
    ]:
        add(TextBlock(item, "bullet"))

    add(TextBlock("10. Aplicativo Android", "h1"))
    add(TextBlock("La app Android esta en cleanHome-androidApp. Se ejecuta desde Android Studio y consume la API usando Retrofit."))
    add(TextBlock("Archivo de configuracion de API:"))
    add(TextBlock("cleanHome-androidApp/app/src/main/java/.../api/ApiConfig.kt", "code"))
    add(TextBlock("Para emulador Android usar:"))
    add(TextBlock('const val BASE_URL = "http://10.0.2.2:3000/api/"', "code"))
    add(TextBlock("Para dispositivo fisico usar una IP local de la PC solo en el entorno local, por ejemplo el formato http://IP_DE_TU_PC:3000/api/. No subir IPs personales al repositorio."))
    for item in [
        "Registro y login de cliente.",
        "Guardado de token JWT y datos de sesion.",
        "Catalogo de servicios desde backend.",
        "Agenda con selectores nativos de fecha y hora.",
        "Historial con fecha corta y colores por estado.",
        "Perfil con navegacion de regreso y cierre de sesion.",
        "Panel admin Android para listar solicitudes, cambiar estado y administrar servicios.",
    ]:
        add(TextBlock(item, "bullet"))

    add(TextBlock("11. SQLite local Android", "h1"))
    add(TextBlock("SQLite local no reemplaza MySQL. Su funcion es conservar datos utiles en el dispositivo y preparar soporte offline."))
    for item in [
        "usuario_sesion: usuario autenticado y token.",
        "servicios: cache del catalogo.",
        "solicitudes: historial local.",
        "solicitudes_pendientes_sync: cola para solicitudes creadas sin red.",
        "sync_metadata: marcas de ultima sincronizacion por modulo.",
    ]:
        add(TextBlock(item, "bullet"))
    add(TextBlock("Archivo de referencia: cleanhome-backend/database/sqlite_android_cleanhome.sql"))

    add(TextBlock("12. Flujo de levantamiento paso a paso", "h1"))
    steps = [
        "1. Iniciar MySQL Server.",
        "2. Crear o actualizar CleanHomeDB con los scripts SQL.",
        "3. Configurar cleanhome-backend/.env.",
        "4. Ejecutar npm install y npm start en cleanhome-backend.",
        "5. Validar http://localhost:3000/api/servicios.",
        "6. Ejecutar npm install y npm start en cleanhome-admin-frontend.",
        "7. Abrir http://localhost:5173 y validar login admin.",
        "8. Crear un servicio desde el panel web.",
        "9. Abrir cleanHome-androidApp en Android Studio.",
        "10. Confirmar ApiConfig.kt con 10.0.2.2 para emulador.",
        "11. Ejecutar la app Android.",
        "12. Registrar o iniciar sesion con cliente.",
        "13. Confirmar que el servicio creado en web aparece en catalogo Android.",
        "14. Agendar una solicitud desde Android.",
        "15. Validar la solicitud en historial Android y en panel admin web.",
    ]
    for item in steps:
        add(TextBlock(item, "p"))

    add(TextBlock("13. Validacion de integracion", "h1"))
    for item in [
        "GET /api/servicios responde desde navegador o Postman.",
        "El panel web crea servicios y se reflejan en MySQL.",
        "Android carga los servicios creados desde web.",
        "Android crea solicitudes con JWT valido.",
        "El historial Android muestra la solicitud con estado y fecha legible.",
        "El panel admin web o Android muestra la solicitud creada.",
        "El cambio de estado se refleja posteriormente en el historial.",
    ]:
        add(TextBlock(item, "bullet"))

    add(TextBlock("14. Seguridad y buenas practicas", "h1"))
    for item in [
        "No subir .env, local.properties, node_modules ni carpetas build.",
        "No subir IPs personales; usar placeholders o 10.0.2.2 para emulador.",
        "Cambiar JWT_SECRET por ambiente.",
        "Cambiar credenciales demo antes de un despliegue real.",
        "No guardar contrasenas en texto plano; el backend usa bcrypt.",
        "Usar HTTPS en despliegues reales.",
        "Configurar CORS segun dominios reales en produccion.",
    ]:
        add(TextBlock(item, "bullet"))

    add(TextBlock("15. Mantenimiento", "h1"))
    for item in [
        "Actualizar README.md cuando cambie la arquitectura o los puertos.",
        "Actualizar cleanhome-backend/README.md cuando cambien endpoints o variables de entorno.",
        "Actualizar cleanHome-androidApp/README.md cuando cambie la URL base, SQLite o flujos Android.",
        "Crear migraciones SQL para cambios en bases existentes.",
        "Probar backend, web y Android antes de hacer merge a main.",
    ]:
        add(TextBlock(item, "bullet"))

    add(TextBlock("16. Solucion de problemas", "h1"))
    troubleshooting = [
        ("Android no conecta al backend", "Usar 10.0.2.2 en emulador. En dispositivo fisico usar IP local de la PC y revisar firewall."),
        ("Login falla", "Verificar usuario, rol, hash de contrasena y JWT_SECRET."),
        ("Servicios no aparecen", "Validar que servicios.activo sea verdadero y que GET /api/servicios responda."),
        ("Solicitud no se crea", "Revisar token JWT, campos obligatorios y logs del backend."),
        ("Panel web no carga datos", "Confirmar API Base en login y que backend este en ejecucion."),
        ("Fecha larga en Android", "Usar formateo de UI en adapters; el backend puede devolver timestamps completos."),
    ]
    for problem, solution in troubleshooting:
        add(TextBlock(f"{problem}: {solution}", "bullet"))

    add(TextBlock("17. Cierre", "h1"))
    add(TextBlock("Con este flujo, CleanHome queda listo para ejecutarse localmente con MySQL, backend Express, panel web administrativo y app Android integrada. Para un despliegue futuro, el cambio central sera publicar backend/base de datos y actualizar la URL base consumida por web y Android."))

    return b


def manual_blocks_from_markdown() -> list[TextBlock]:
    blocks: list[TextBlock] = []
    in_code = False

    for raw_line in SOURCE.read_text(encoding="utf-8").splitlines():
        line = raw_line.rstrip()
        if not line.strip():
            continue
        if line.strip() == "```":
            in_code = not in_code
            continue
        if in_code:
            blocks.append(TextBlock(line, "code"))
            continue
        if line.startswith("# "):
            blocks.append(TextBlock(line[2:].strip(), "title"))
        elif line.startswith("## "):
            blocks.append(TextBlock(line[3:].strip(), "h1"))
        elif line.startswith("### "):
            blocks.append(TextBlock(line[4:].strip(), "h1"))
        elif line.startswith("- "):
            blocks.append(TextBlock(line[2:].strip(), "bullet"))
        else:
            blocks.append(TextBlock(line.strip(), "p"))

    return blocks


def split_pages(blocks: list[TextBlock]) -> list[list[tuple[str, str]]]:
    pages: list[list[tuple[str, str]]] = []
    page: list[tuple[str, str]] = []
    y = PAGE_H - M_TOP
    width_chars = 86

    def need(lines: int, extra: int = 0) -> None:
        nonlocal page, y
        if y - (lines * LINE_H) - extra < M_BOTTOM:
            pages.append(page)
            page = []
            y = PAGE_H - M_TOP

    for block in blocks:
        text = asciiish(block.text)
        if block.kind == "title":
            lines = [text]
            need(2, 18)
            page.append((block.kind, text))
            y -= 34
        elif block.kind == "subtitle":
            page.append((block.kind, text))
            y -= 24
        elif block.kind == "meta":
            page.append((block.kind, text))
            y -= 18
        elif block.kind == "h1":
            need(3, 18)
            page.append((block.kind, text))
            y -= 28
        elif block.kind == "code":
            lines = wrap(text, width_chars, replace_whitespace=False) or [""]
            need(len(lines), 8)
            for line in lines:
                page.append((block.kind, line))
                y -= LINE_H
            y -= 3
        elif block.kind == "bullet":
            lines = wrap(text, width_chars - 4) or [""]
            need(len(lines), 6)
            page.append((block.kind, "* " + lines[0]))
            y -= LINE_H
            for line in lines[1:]:
                page.append(("bullet_cont", "  " + line))
                y -= LINE_H
            y -= 3
        else:
            lines = wrap(text, width_chars) or [""]
            need(len(lines), 8)
            for line in lines:
                page.append((block.kind, line))
                y -= LINE_H
            y -= 5

    if page:
        pages.append(page)
    return pages


def page_stream(lines: list[tuple[str, str]], page_num: int, total: int) -> bytes:
    ops: list[str] = []
    y = PAGE_H - M_TOP
    ops.append("BT /F2 9 Tf 58 760 Td (CleanHome - Manual Tecnico) Tj ET")
    ops.append("0.75 w 58 750 m 554 750 l S")
    y = 724
    for kind, text in lines:
        if kind == "title":
            size, font, color = 22, "F2", "0.04 0.09 0.17"
            x = M_LEFT
        elif kind == "subtitle":
            size, font, color = 12, "F1", "0.22 0.26 0.32"
            x = M_LEFT
        elif kind == "meta":
            size, font, color = 10, "F1", "0.35 0.39 0.45"
            x = M_LEFT
        elif kind == "h1":
            size, font, color = 15, "F2", "0.00 0.32 0.62"
            x = M_LEFT
            y -= 6
        elif kind == "code":
            size, font, color = 9, "F3", "0.20 0.24 0.30"
            x = M_LEFT + 14
        elif kind in {"bullet", "bullet_cont"}:
            size, font, color = 10, "F1", "0.12 0.16 0.22"
            x = M_LEFT + 16
        else:
            size, font, color = 10, "F1", "0.12 0.16 0.22"
            x = M_LEFT

        ops.append(f"{color} rg BT /{font} {size} Tf {x} {y} Td ({esc_pdf(text)}) Tj ET")
        if kind == "title":
            y -= 31
        elif kind == "subtitle":
            y -= 23
        elif kind == "meta":
            y -= 17
        elif kind == "h1":
            y -= 24
        else:
            y -= LINE_H

    ops.append(f"0.35 0.39 0.45 rg BT /F1 9 Tf 500 32 Td (Pagina {page_num} de {total}) Tj ET")
    return ("\n".join(ops) + "\n").encode("cp1252", errors="replace")


def build_pdf(pages: list[list[tuple[str, str]]]) -> bytes:
    objects: list[bytes] = []

    def add(obj: bytes) -> int:
        objects.append(obj)
        return len(objects)

    catalog_id = add(b"<< /Type /Catalog /Pages 2 0 R >>")
    pages_id = add(b"")
    f1_id = add(b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>")
    f2_id = add(b"<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>")
    f3_id = add(b"<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>")

    page_ids: list[int] = []
    for idx, page in enumerate(pages, 1):
        stream = page_stream(page, idx, len(pages))
        content_id = add(b"<< /Length " + str(len(stream)).encode() + b" >>\nstream\n" + stream + b"endstream")
        page_obj = (
            f"<< /Type /Page /Parent 2 0 R /MediaBox [0 0 {PAGE_W} {PAGE_H}] "
            f"/Resources << /Font << /F1 {f1_id} 0 R /F2 {f2_id} 0 R /F3 {f3_id} 0 R >> >> "
            f"/Contents {content_id} 0 R >>"
        ).encode()
        page_ids.append(add(page_obj))

    kids = " ".join(f"{pid} 0 R" for pid in page_ids)
    objects[pages_id - 1] = f"<< /Type /Pages /Kids [{kids}] /Count {len(page_ids)} >>".encode()

    out = bytearray(b"%PDF-1.4\n%\xe2\xe3\xcf\xd3\n")
    offsets = [0]
    for i, obj in enumerate(objects, 1):
        offsets.append(len(out))
        out.extend(f"{i} 0 obj\n".encode())
        out.extend(obj)
        out.extend(b"\nendobj\n")
    xref = len(out)
    out.extend(f"xref\n0 {len(objects)+1}\n".encode())
    out.extend(b"0000000000 65535 f \n")
    for offset in offsets[1:]:
        out.extend(f"{offset:010d} 00000 n \n".encode())
    out.extend(
        f"trailer\n<< /Size {len(objects)+1} /Root {catalog_id} 0 R >>\nstartxref\n{xref}\n%%EOF\n".encode()
    )
    return bytes(out)


def main() -> None:
    OUT.parent.mkdir(parents=True, exist_ok=True)
    blocks = manual_blocks_from_markdown() if SOURCE.exists() else manual_blocks()
    pages = split_pages(blocks)
    OUT.write_bytes(build_pdf(pages))
    print(f"PDF generado: {OUT}")
    print(f"Paginas: {len(pages)}")


if __name__ == "__main__":
    main()
