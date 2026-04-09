# ⚽ Mundial 2026 Hub — Angular Frontend

Plataforma web para el seguimiento del Mundial FIFA 2026.
Proyecto académico - Universidad El Bosque, Ingeniería de Sistemas.

---

## 🚀 Cómo ejecutar en VS Code

### Requisitos previos
- **Node.js** v18 o superior → https://nodejs.org
- **npm** (viene con Node)
- **VS Code** → https://code.visualstudio.com

### Pasos para ver el proyecto

```bash
# 1. Abre la carpeta del proyecto en VS Code
#    File > Open Folder > selecciona "mundial-2026-hub"

# 2. Abre la terminal integrada
#    Terminal > New Terminal  (o Ctrl+`)

# 3. Instala las dependencias
npm install

# 4. Levanta el servidor de desarrollo
npm start

# 5. Abre en el navegador
#    http://localhost:4200
```

### Extensiones de VS Code recomendadas
Instálalas desde la pestaña Extensiones (Ctrl+Shift+X):

| Extensión | ID |
|-----------|-----|
| Angular Language Service | `angular.ng-template` |
| ESLint | `dbaeumer.vscode-eslint` |
| Prettier | `esbenp.prettier-vscode` |
| SCSS IntelliSense | `mrmlnc.vscode-scss` |
| Material Icon Theme | `pkief.material-icon-theme` |
| GitLens | `eamodio.gitlens` |

---

## 📁 Estructura del Proyecto

```
src/
├── app/
│   ├── core/                     # Servicios, modelos, guards, interceptors
│   │   ├── guards/               # authGuard, operadorGuard, soporteGuard
│   │   ├── interceptors/         # authInterceptor (token + logs)
│   │   ├── models/               # Interfaces TypeScript del dominio
│   │   └── services/             # AuthService, PartidosService, etc.
│   │
│   ├── shared/                   # Componentes reutilizables
│   │   └── components/
│   │       ├── layout/           # Layout principal con sidebar
│   │       ├── sidebar/          # Menú lateral con navegación
│   │       └── not-found/        # Página 404
│   │
│   └── features/                 # Módulos funcionales (lazy-loaded)
│       ├── auth/                 # Login y Registro
│       │   ├── login/
│       │   └── registro/
│       ├── dashboard/            # Pantalla principal
│       ├── partidos/             # Fixture y detalle de partidos
│       ├── agenda/               # Agenda personalizada
│       ├── entradas/             # Gestión de entradas (reserva/pago/transferencia)
│       ├── notificaciones/       # Centro de notificaciones
│       ├── pollas/               # Pollas futboleras (predicciones)
│       ├── album/                # Álbum digital y intercambios
│       ├── perfil/               # Perfil y preferencias de usuario
│       └── admin/                # Backoffice (operadores/soporte)
│
├── environments/                 # Variables de entorno dev/prod
└── styles.scss                   # Estilos globales y variables CSS
```

---

## 🗺️ Rutas de la Aplicación

| Ruta | Módulo | Acceso |
|------|--------|--------|
| `/auth/login` | Auth | Público |
| `/auth/registro` | Auth | Público |
| `/dashboard` | Dashboard | Autenticado |
| `/partidos` | Partidos | Autenticado |
| `/partidos/:id` | Partidos | Autenticado |
| `/agenda` | Agenda | Autenticado |
| `/entradas` | Entradas | Autenticado |
| `/entradas/:id` | Entradas | Autenticado |
| `/notificaciones` | Notificaciones | Autenticado |
| `/pollas` | Pollas | Autenticado |
| `/pollas/nueva` | Pollas | Autenticado |
| `/pollas/:id` | Pollas | Autenticado |
| `/album` | Álbum | Autenticado |
| `/album/intercambio` | Álbum | Autenticado |
| `/perfil` | Perfil | Autenticado |
| `/admin` | Backoffice | Operador/Admin |

---

## 🔧 Servicios Principales

| Servicio | Responsabilidad |
|----------|----------------|
| `AuthService` | Login, registro, sesión con signals |
| `PartidosService` | Fixture, en vivo, filtros, caché |
| `EntradasService` | Ciclo de vida de entradas (reserva→pago→transferencia) |
| `PollasService` | Crear, unirse, pronósticos, ranking |
| `AlbumService` | Abrir paquetes, colección, intercambios |
| `NotificacionesService` | Centro de notificaciones, conteo de no leídas |

---

## 🌐 Integraciones Externas

Configuradas en `src/environments/environment.ts`:

- **football-data.org** → Datos de partidos y fixture
- **Firebase FCM** → Notificaciones push
- **Stripe (test mode)** → Pagos simulados
- **Wiremock** → APIs mockeadas para desarrollo
- **SendGrid** → Email transaccional (opcional)
- **OpenStreetMap** → Mapas de estadios/ciudades

---

## 🏗️ Tecnologías

- **Angular 17** (Standalone Components, Signals, lazy loading)
- **SCSS** con variables CSS
- **RxJS** para flujos reactivos
- **Angular Router** con guards funcionales
- **HttpClient** con interceptors

---

## 📝 Comandos Útiles

```bash
npm start              # Servidor de desarrollo en :4200
npm run build          # Build de producción
npm test               # Pruebas unitarias con Karma
ng generate component features/partidos/lista/partidos-lista  # Generar componente
ng generate service core/services/mi-servicio                 # Generar servicio
```
