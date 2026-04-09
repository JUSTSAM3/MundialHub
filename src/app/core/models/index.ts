// ============================================
// MODELOS DE DOMINIO - Mundial 2026 Hub
// ============================================

// ---- USUARIO ----
export interface Usuario {
  id: string;
  email: string;
  nombre: string;
  apellido: string;
  avatar?: string;
  preferencias: Preferencias;
  rol: RolUsuario;
  creadoEn: Date;
  ultimoAcceso: Date;
}

export type RolUsuario = 'aficionado' | 'operador' | 'soporte' | 'admin' | 'compliance';

export interface Preferencias {
  equiposFavoritos: string[];      // códigos ISO de selecciones
  ciudadesInteres: string[];
  estadiosInteres: string[];
  canalesNotificacion: CanalNotificacion[];
  zonaHoraria: string;
  idioma: string;
}

export type CanalNotificacion = 'push' | 'email' | 'sms';

// ---- PARTIDO ----
export interface Partido {
  id: string;
  fechaHora: Date;
  estadio: Estadio;
  equipoLocal: Equipo;
  equipoVisitante: Equipo;
  fase: FasePartido;
  estado: EstadoPartido;
  marcador?: Marcador;
  grupo?: string;
}

export type EstadoPartido = 'programado' | 'en_juego' | 'finalizado' | 'suspendido' | 'aplazado';
export type FasePartido = 'fase_grupos' | 'octavos' | 'cuartos' | 'semifinal' | 'tercer_puesto' | 'final';

export interface Marcador {
  golesLocal: number;
  golesVisitante: number;
  minuto?: number;
}

export interface Equipo {
  id: string;
  nombre: string;
  codigoISO: string;
  banderaUrl?: string;
  grupo?: string;
}

export interface Estadio {
  id: string;
  nombre: string;
  ciudad: string;
  pais: string;
  capacidad: number;
  latitud: number;
  longitud: number;
}

// ---- ENTRADA ----
export type EstadoEntrada = 'disponible' | 'reservada' | 'pagada' | 'transferida' | 'reembolsada' | 'expirada';

export interface Entrada {
  id: string;
  partidoId: string;
  titularId: string;
  sector: string;
  fila: string;
  asiento: string;
  precio: number;
  estado: EstadoEntrada;
  reservadaEn?: Date;
  ttlExpiracion?: Date;
  correlacionId: string;
  historialEstados: TransicionEstado[];
}

export interface TransicionEstado {
  estadoAnterior: EstadoEntrada;
  estadoNuevo: EstadoEntrada;
  fechaHora: Date;
  motivo?: string;
  usuarioId: string;
}

// ---- NOTIFICACIÓN ----
export interface Notificacion {
  id: string;
  tipo: TipoNotificacion;
  titulo: string;
  cuerpo: string;
  destinatarioId: string;
  canal: CanalNotificacion;
  estado: EstadoNotificacion;
  creadaEn: Date;
  enviadaEn?: Date;
  leidaEn?: Date;
  partidoId?: string;
}

export type TipoNotificacion = 'gol' | 'inicio_partido' | 'cambio_horario' | 'entrada' | 'polla' | 'album' | 'sistema';
export type EstadoNotificacion = 'pendiente' | 'enviada' | 'entregada' | 'fallida' | 'leida';

// ---- POLLA ----
export interface Polla {
  id: string;
  nombre: string;
  creadorId: string;
  codigoInvitacion: string;
  miembros: MiembroPolla[];
  pronosticos: Pronostico[];
  estado: 'activa' | 'finalizada';
  creadaEn: Date;
}

export interface MiembroPolla {
  usuarioId: string;
  nombre: string;
  puntosTotales: number;
  posicion: number;
  unidoEn: Date;
}

export interface Pronostico {
  id: string;
  pollaId: string;
  usuarioId: string;
  partidoId: string;
  golesLocal: number;
  golesVisitante: number;
  bloqueado: boolean;     // true cuando cierra el plazo antes del partido
  puntos?: number;        // calculado al finalizar el partido
  creadoEn: Date;
}

// ---- ÁLBUM DIGITAL ----
export interface Album {
  id: string;
  usuarioId: string;
  paginas: PaginaAlbum[];
  totalLaminas: number;
  laminasObtenidas: number;
  paquetesPendientes: number;
}

export interface PaginaAlbum {
  id: string;
  titulo: string;
  categoria: CategoriaAlbum;
  slots: SlotLamina[];
}

export type CategoriaAlbum = 'seleccion' | 'estadio' | 'figura' | 'especial';

export interface SlotLamina {
  posicion: number;
  laminaId?: string;
  completado: boolean;
}

export interface Lamina {
  id: string;
  numero: number;
  nombre: string;
  descripcion: string;
  categoria: CategoriaAlbum;
  rareza: RarezaLamina;
  imagenUrl: string;
  equipoId?: string;
}

export type RarezaLamina = 'comun' | 'poco_comun' | 'rara' | 'epica' | 'legendaria';

export interface LaminaUsuario {
  id: string;
  usuarioId: string;
  laminaId: string;
  cantidad: number;
  repetidas: number;
  obtenidaEn: Date;
}

export interface Paquete {
  id: string;
  usuarioId: string;
  cantidadLaminas: number;
  motivo: MotivoPaquete;
  abierto: boolean;
  obtenidoEn: Date;
}

export type MotivoPaquete = 'login_diario' | 'prediccion_completada' | 'codigo_promocional' | 'premio_polla';

export interface Intercambio {
  id: string;
  solicitanteId: string;
  receptorId: string;
  laminaOfrecidaId: string;
  laminaSolicitadaId: string;
  estado: 'pendiente' | 'aceptado' | 'rechazado' | 'cancelado';
  creadoEn: Date;
}

// ---- EVENTO DE AUDITORÍA ----
export interface EventoAuditoria {
  id: string;
  tipo: string;
  entidadTipo: string;
  entidadId: string;
  usuarioId: string;
  datos: Record<string, unknown>;
  correlacionId: string;
  fechaHora: Date;
}

// ---- PAGINACIÓN ----
export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  pagina: number;
  tamano: number;
  totalPaginas: number;
}

// ---- API RESPONSE ----
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  mensaje?: string;
  timestamp: Date;
}
