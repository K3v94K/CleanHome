const ESTADOS_VALIDOS = ['Pendiente', 'Confirmada', 'En proceso', 'Completada', 'Cancelada'];
const TOKEN_KEY = 'cleanhome_admin_token';
const API_KEY = 'cleanhome_api_base';

// Estados disponibles para actualizar solicitudes desde el panel.
// TOKEN_KEY y API_KEY guardan sesion/configuracion en localStorage.
const dom = {
  loginView: document.getElementById('loginView'),
  dashboardView: document.getElementById('dashboardView'),
  loginForm: document.getElementById('loginForm'),
  baseApiInput: document.getElementById('baseApiInput'),
  correoInput: document.getElementById('correoInput'),
  passwordInput: document.getElementById('passwordInput'),
  sessionInfo: document.getElementById('sessionInfo'),
  logoutBtn: document.getElementById('logoutBtn'),
  statusLine: document.getElementById('statusLine'),
  tabButtons: document.querySelectorAll('.tab-btn'),
  solicitudesSection: document.getElementById('solicitudesSection'),
  serviciosSection: document.getElementById('serviciosSection'),
  administradoresSection: document.getElementById('administradoresSection'),

  solicitudesBody: document.getElementById('solicitudesTableBody'),
  reloadSolicitudesBtn: document.getElementById('reloadSolicitudesBtn'),

  serviciosBody: document.getElementById('serviciosTableBody'),
  reloadServiciosBtn: document.getElementById('reloadServiciosBtn'),
  servicioForm: document.getElementById('servicioForm'),
  servicioIdInput: document.getElementById('servicioIdInput'),
  servicioNombreInput: document.getElementById('servicioNombreInput'),
  servicioDuracionInput: document.getElementById('servicioDuracionInput'),
  servicioDescripcionInput: document.getElementById('servicioDescripcionInput'),
  servicioPrecioInput: document.getElementById('servicioPrecioInput'),
  servicioActivoInput: document.getElementById('servicioActivoInput'),
  clearServicioBtn: document.getElementById('clearServicioBtn'),

  adminsBody: document.getElementById('adminsTableBody'),
  reloadAdminsBtn: document.getElementById('reloadAdminsBtn'),
  adminUserForm: document.getElementById('adminUserForm'),
  adminNombreInput: document.getElementById('adminNombreInput'),
  adminCorreoInput: document.getElementById('adminCorreoInput'),
  adminTelefonoInput: document.getElementById('adminTelefonoInput'),
  adminPasswordInput: document.getElementById('adminPasswordInput'),
  adminDireccionInput: document.getElementById('adminDireccionInput'),
  clearAdminUserBtn: document.getElementById('clearAdminUserBtn'),
};

// Estado global en memoria del frontend.
const state = {
  token: localStorage.getItem(TOKEN_KEY) || '',
  baseApi: localStorage.getItem(API_KEY) || 'http://localhost:3000/api',
  personal: [],
  solicitudes: [],
  servicios: [],
  administradores: [],
  activeView: 'solicitudes',
};

// Muestra mensajes informativos, de exito o error en la barra inferior.
function setStatus(message, type = 'info') {
  dom.statusLine.textContent = message;
  dom.statusLine.classList.remove('ok', 'error');
  if (type === 'ok') dom.statusLine.classList.add('ok');
  if (type === 'error') dom.statusLine.classList.add('error');
}

function setSessionInfo(text) {
  dom.sessionInfo.textContent = text;
}

// Alterna entre vista de login y vista de dashboard.
function toggleView(isLogged) {
  dom.loginView.classList.toggle('hidden', isLogged);
  dom.dashboardView.classList.toggle('hidden', !isLogged);
  dom.logoutBtn.classList.toggle('hidden', !isLogged);
}

function showAdminView(viewName) {
  state.activeView = viewName;
  dom.solicitudesSection.classList.toggle('hidden', viewName !== 'solicitudes');
  dom.serviciosSection.classList.toggle('hidden', viewName !== 'servicios');
  dom.administradoresSection.classList.toggle('hidden', viewName !== 'administradores');

  dom.tabButtons.forEach((button) => {
    button.classList.toggle('active', button.dataset.view === viewName);
  });
}

// Elimina "/" final para evitar URLs mal formadas al concatenar rutas.
function normalizeBaseApi(url) {
  return url.trim().replace(/\/+$/, '');
}

function formatDate(isoDate) {
  if (!isoDate) return '-';
  const date = new Date(isoDate);
  if (Number.isNaN(date.getTime())) return isoDate;
  return date.toLocaleDateString('es-SV');
}

// Sanitiza texto para inyectarlo seguro en HTML.
function escapeHtml(value) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// Cliente HTTP basico para consumir la API backend.
async function apiFetch(path, options = {}, useAuth = true) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  if (useAuth && state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }

  const response = await fetch(`${state.baseApi}${path}`, {
    ...options,
    headers,
  });

  let payload = {};
  try {
    payload = await response.json();
  } catch (_) {
    payload = {};
  }

  if (!response.ok) {
    const message = payload.message || `Error ${response.status}`;
    throw new Error(message);
  }

  return payload;
}

// Construye tabla de solicitudes en pantalla.
function renderSolicitudes() {
  if (!state.solicitudes.length) {
    dom.solicitudesBody.innerHTML = '<tr><td colspan="8">No hay solicitudes registradas.</td></tr>';
    return;
  }

  const personalOptions = state.personal
    .map(
      (p) =>
        `<option value="${p.id_personal}">${escapeHtml(p.nombre)}${p.disponible ? '' : ' (No disponible)'}</option>`
    )
    .join('');

  dom.solicitudesBody.innerHTML = state.solicitudes
    .map((sol) => {
      const estadoOptions = ESTADOS_VALIDOS
        .map((estado) => `<option value="${estado}" ${estado === sol.estado ? 'selected' : ''}>${estado}</option>`)
        .join('');

      const personalSelect = `<select class="personal-select" data-id="${sol.id_solicitud}">
          <option value="">Sin asignar</option>
          ${personalOptions}
        </select>`;

      return `<tr>
        <td>${sol.id_solicitud}</td>
        <td>${escapeHtml(sol.cliente)}</td>
        <td>${escapeHtml(sol.servicio)}</td>
        <td>${formatDate(sol.fecha_servicio)}</td>
        <td>${escapeHtml(sol.hora_servicio)}</td>
        <td>
          <select class="estado-select" data-id="${sol.id_solicitud}">${estadoOptions}</select>
        </td>
        <td>
          ${personalSelect}
          <small>${escapeHtml(sol.personal_nombre || 'No asignado')}</small>
        </td>
        <td>
          <div class="actions-inline">
            <button class="btn secondary" data-action="estado" data-id="${sol.id_solicitud}" type="button">Guardar estado</button>
            <button class="btn secondary" data-action="personal" data-id="${sol.id_solicitud}" type="button">Asignar</button>
          </div>
        </td>
      </tr>`;
    })
    .join('');

  state.solicitudes.forEach((sol) => {
    if (!sol.id_personal) return;
    const select = dom.solicitudesBody.querySelector(`.personal-select[data-id="${sol.id_solicitud}"]`);
    if (select) select.value = String(sol.id_personal);
  });
}

// Construye tabla de servicios en pantalla.
function renderServicios() {
  if (!state.servicios.length) {
    dom.serviciosBody.innerHTML = '<tr><td colspan="5">No hay servicios.</td></tr>';
    return;
  }

  dom.serviciosBody.innerHTML = state.servicios
    .map(
      (s) => `<tr>
      <td>${s.id_servicio}</td>
      <td>${escapeHtml(s.nombre)}</td>
      <td>$${Number(s.precio).toFixed(2)}</td>
      <td>${s.activo ? 'Activo' : 'Inactivo'}</td>
      <td>
        <div class="actions-inline">
          <button class="btn secondary" data-action="edit-servicio" data-id="${s.id_servicio}" type="button">Editar</button>
          <button class="btn warn" data-action="delete-servicio" data-id="${s.id_servicio}" type="button" ${s.activo ? '' : 'disabled'}>Desactivar</button>
        </div>
      </td>
    </tr>`
    )
    .join('');
}

function renderAdministradores() {
  if (!state.administradores.length) {
    dom.adminsBody.innerHTML = '<tr><td colspan="5">No hay administradores registrados.</td></tr>';
    return;
  }

  dom.adminsBody.innerHTML = state.administradores
    .map(
      (admin) => `<tr>
      <td>${admin.id_usuario}</td>
      <td>${escapeHtml(admin.nombre)}</td>
      <td>${escapeHtml(admin.correo)}</td>
      <td>${escapeHtml(admin.telefono || '-')}</td>
      <td>${formatDate(admin.fecha_creacion)}</td>
    </tr>`
    )
    .join('');
}

// Carga solicitudes y personal de limpieza para acciones de asignacion.
async function loadSolicitudes() {
  const solicitudesResp = await apiFetch('/admin/solicitudes');
  state.solicitudes = solicitudesResp.solicitudes || [];

  try {
    const personalResp = await apiFetch('/admin/personal');
    state.personal = personalResp.personal || [];
  } catch (error) {
    state.personal = [];
    setStatus(`Solicitudes cargadas, pero no se pudo cargar personal: ${error.message}`, 'error');
  }

  renderSolicitudes();
}

// Carga catalogo de servicios para administracion.
async function loadServicios() {
  const serviciosResp = await apiFetch('/admin/servicios');
  state.servicios = serviciosResp.servicios || [];
  renderServicios();
}

async function loadAdministradores() {
  const adminsResp = await apiFetch('/admin/usuarios/admin');
  state.administradores = adminsResp.administradores || [];
  renderAdministradores();
}

// Carga inicial de datos del dashboard (solicitudes + servicios).
async function loadDashboard() {
  await Promise.all([loadSolicitudes(), loadServicios(), loadAdministradores()]);
  showAdminView(state.activeView);
}

// Limpia formulario de servicio para crear uno nuevo.
function resetServicioForm() {
  dom.servicioIdInput.value = '';
  dom.servicioNombreInput.value = '';
  dom.servicioDuracionInput.value = '';
  dom.servicioDescripcionInput.value = '';
  dom.servicioPrecioInput.value = '';
  dom.servicioActivoInput.checked = true;
}

function resetAdminUserForm() {
  dom.adminNombreInput.value = '';
  dom.adminCorreoInput.value = '';
  dom.adminTelefonoInput.value = '';
  dom.adminPasswordInput.value = '';
  dom.adminDireccionInput.value = '';
}

// Rellena formulario para editar un servicio existente.
function fillServicioForm(servicio) {
  dom.servicioIdInput.value = servicio.id_servicio;
  dom.servicioNombreInput.value = servicio.nombre || '';
  dom.servicioDuracionInput.value = servicio.duracion_estimada || '';
  dom.servicioDescripcionInput.value = servicio.descripcion || '';
  dom.servicioPrecioInput.value = servicio.precio ?? '';
  dom.servicioActivoInput.checked = Boolean(servicio.activo);
}

// Cierra sesion local del panel.
function signOut(message = 'Sesion finalizada.') {
  state.token = '';
  localStorage.removeItem(TOKEN_KEY);
  setSessionInfo('Sesion no iniciada');
  toggleView(false);
  setStatus(message);
}

// Maneja login admin y carga del dashboard.
async function handleLoginSubmit(event) {
  event.preventDefault();

  try {
    state.baseApi = normalizeBaseApi(dom.baseApiInput.value);
    localStorage.setItem(API_KEY, state.baseApi);

    const payload = await apiFetch(
      '/auth/login',
      {
        method: 'POST',
        body: JSON.stringify({
          correo: dom.correoInput.value.trim(),
          password: dom.passwordInput.value,
        }),
      },
      false
    );

    if (!payload.usuario || payload.usuario.nombre_rol !== 'Admin') {
      throw new Error('La cuenta no tiene rol de administrador.');
    }

    state.token = payload.token;
    localStorage.setItem(TOKEN_KEY, state.token);
    setSessionInfo(`Admin: ${payload.usuario.nombre}`);

    toggleView(true);
    await loadDashboard();
    setStatus('Sesion iniciada y panel cargado correctamente.', 'ok');
    dom.passwordInput.value = '';
  } catch (error) {
    setStatus(error.message, 'error');
  }
}

// Atiende botones de acciones en la tabla de solicitudes.
async function handleSolicitudesActions(event) {
  const button = event.target.closest('button[data-action]');
  if (!button) return;

  const id = button.dataset.id;
  const action = button.dataset.action;

  try {
    if (action === 'estado') {
      const select = dom.solicitudesBody.querySelector(`.estado-select[data-id="${id}"]`);
      const estado = select ? select.value : '';
      await apiFetch(`/admin/solicitudes/${id}/estado`, {
        method: 'PATCH',
        body: JSON.stringify({ estado }),
      });
      setStatus(`Estado de solicitud #${id} actualizado.`, 'ok');
    }

    if (action === 'personal') {
      const select = dom.solicitudesBody.querySelector(`.personal-select[data-id="${id}"]`);
      if (!select || !select.value) {
        throw new Error('Selecciona un miembro del personal antes de asignar.');
      }

      await apiFetch(`/admin/solicitudes/${id}/asignar-personal`, {
        method: 'PATCH',
        body: JSON.stringify({ id_personal: Number(select.value) }),
      });
      setStatus(`Personal asignado a solicitud #${id}.`, 'ok');
    }

    await loadSolicitudes();
  } catch (error) {
    if (error.message.toLowerCase().includes('token')) {
      signOut('Tu sesion expiro. Vuelve a iniciar sesion.');
      return;
    }
    setStatus(error.message, 'error');
  }
}

// Atiende acciones editar/desactivar en la tabla de servicios.
async function handleServiciosActions(event) {
  const button = event.target.closest('button[data-action]');
  if (!button) return;

  const id = Number(button.dataset.id);
  const servicio = state.servicios.find((item) => item.id_servicio === id);
  if (!servicio) return;

  try {
    if (button.dataset.action === 'edit-servicio') {
      fillServicioForm(servicio);
      setStatus(`Editando servicio #${id}`);
      return;
    }

    if (button.dataset.action === 'delete-servicio') {
      const confirmed = window.confirm(`Deseas desactivar el servicio "${servicio.nombre}"?`);
      if (!confirmed) return;

      await apiFetch(`/admin/servicios/${id}`, { method: 'DELETE' });
      await loadServicios();
      setStatus(`Servicio #${id} desactivado.`, 'ok');
    }
  } catch (error) {
    setStatus(error.message, 'error');
  }
}

// Crea o actualiza servicios segun exista id en el formulario.
async function handleServicioSubmit(event) {
  event.preventDefault();

  try {
    const id = dom.servicioIdInput.value;
    const payload = {
      nombre: dom.servicioNombreInput.value.trim(),
      descripcion: dom.servicioDescripcionInput.value.trim(),
      duracion_estimada: dom.servicioDuracionInput.value.trim(),
      precio: Number(dom.servicioPrecioInput.value),
      activo: dom.servicioActivoInput.checked,
    };

    if (!payload.nombre || Number.isNaN(payload.precio)) {
      throw new Error('Nombre y precio valido son obligatorios.');
    }

    if (id) {
      await apiFetch(`/admin/servicios/${id}`, {
        method: 'PUT',
        body: JSON.stringify(payload),
      });
      setStatus(`Servicio #${id} actualizado.`, 'ok');
    } else {
      await apiFetch('/admin/servicios', {
        method: 'POST',
        body: JSON.stringify(payload),
      });
      setStatus('Servicio creado correctamente.', 'ok');
    }

    resetServicioForm();
    await loadServicios();
  } catch (error) {
    setStatus(error.message, 'error');
  }
}

async function handleAdminUserSubmit(event) {
  event.preventDefault();

  try {
    const payload = {
      nombre: dom.adminNombreInput.value.trim(),
      correo: dom.adminCorreoInput.value.trim(),
      telefono: dom.adminTelefonoInput.value.trim(),
      direccion: dom.adminDireccionInput.value.trim(),
      password: dom.adminPasswordInput.value,
    };

    if (!payload.nombre || !payload.correo || !payload.password) {
      throw new Error('Nombre, correo y contrasena son obligatorios.');
    }

    await apiFetch('/admin/usuarios/admin', {
      method: 'POST',
      body: JSON.stringify(payload),
    });

    resetAdminUserForm();
    await loadAdministradores();
    setStatus('Administrador creado correctamente.', 'ok');
  } catch (error) {
    setStatus(error.message, 'error');
  }
}

// Punto de entrada del frontend: registra eventos y recupera sesion.
async function init() {
  dom.baseApiInput.value = state.baseApi;

  dom.loginForm.addEventListener('submit', handleLoginSubmit);
  dom.logoutBtn.addEventListener('click', () => signOut());
  dom.tabButtons.forEach((button) => {
    button.addEventListener('click', () => showAdminView(button.dataset.view));
  });

  dom.reloadSolicitudesBtn.addEventListener('click', async () => {
    try {
      await loadSolicitudes();
      setStatus('Solicitudes recargadas.', 'ok');
    } catch (error) {
      setStatus(error.message, 'error');
    }
  });

  dom.reloadServiciosBtn.addEventListener('click', async () => {
    try {
      await loadServicios();
      setStatus('Servicios recargados.', 'ok');
    } catch (error) {
      setStatus(error.message, 'error');
    }
  });

  dom.reloadAdminsBtn.addEventListener('click', async () => {
    try {
      await loadAdministradores();
      setStatus('Administradores recargados.', 'ok');
    } catch (error) {
      setStatus(error.message, 'error');
    }
  });

  dom.solicitudesBody.addEventListener('click', handleSolicitudesActions);
  dom.serviciosBody.addEventListener('click', handleServiciosActions);
  dom.servicioForm.addEventListener('submit', handleServicioSubmit);
  dom.clearServicioBtn.addEventListener('click', resetServicioForm);
  dom.adminUserForm.addEventListener('submit', handleAdminUserSubmit);
  dom.clearAdminUserBtn.addEventListener('click', resetAdminUserForm);

  if (!state.token) {
    toggleView(false);
    return;
  }

  try {
    toggleView(true);
    await loadDashboard();
    setSessionInfo('Sesion activa');
    setStatus('Sesion recuperada correctamente.', 'ok');
  } catch (error) {
    signOut('La sesion guardada ya no es valida.');
  }
}

init();
