# Contrato API REST – Sanaesencia (borrador v0.1)

Base URL (emulador Android): `http://10.0.2.2:8080/api`
Formato: JSON, UTF-8. Fechas ISO‑8601 (`2026-10-20T15:30:00`). Solo datos ficticios.

## Modelos

| Modelo | Campos |
|---|---|
| Servicio | id, nombre, especialidad, descripcion, duracionMin, precio, modalidad (`PRESENCIAL`/`ONLINE`/`AMBAS`), terapeutaId |
| Terapeuta | id, nombre, especialidad, bio, modalidad |
| Box | id (1..8), nombre |
| Producto | id, nombre, categoria (`KIT`/`ROLL_ON`/`BRUMA`), descripcion, precio, stock |
| Paciente | id, nombre (ficticio), email (ficticio), rol (`PACIENTE`/`TERAPEUTA`/`ADMIN`) |
| Cita | id, pacienteId, terapeutaId, servicioId, boxId (nulo si ONLINE), inicio, fin, modalidad, estado (`AGENDADA`/`COMPLETADA`/`CANCELADA`/`NO_ASISTIO`) |
| AlertaDesercion | pacienteId, nombre, sesionesCompletadas, ultimaSesion, diasInactivo, nivel (`BAJO`/`MEDIO`/`ALTO`) |
| Promocion | id, titulo, descripcion, descuentoPct, vigenteDesde, vigenteHasta, segmento |

## Endpoints

### Paciente
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/auth/login` | Login de prueba → `{token, rol, usuarioId}` |
| GET | `/servicios?especialidad=&modalidad=` | Catálogo de terapias |
| GET | `/terapeutas` | Equipo |
| GET | `/productos` | Catálogo de productos |
| GET | `/disponibilidad?terapeutaId=&fecha=&modalidad=` | Horarios libres |
| POST | `/citas` | Agendar (valida box libre si es presencial) |
| GET | `/pacientes/{id}/citas` | Historial y próximas |
| PATCH | `/citas/{id}/cancelar` | Cancelar |
| GET | `/pacientes/{id}/promociones` | Promociones personalizadas |
| POST | `/dispositivos` | Registrar token push (FCM) |

### Terapeuta
| GET | `/terapeutas/{id}/pacientes` | Pacientes asignados con continuidad |
|---|---|---|
| GET | `/terapeutas/{id}/agenda?fecha=` | Agenda del día |

### Administración
| GET | `/admin/ocupacion?desde=&hasta=` | % ocupación por box y por día |
|---|---|---|
| GET | `/admin/desercion` | Lista de `AlertaDesercion` |
| POST | `/admin/promociones` | Crear promoción para baja demanda |

## Regla de deserción (v0.1, a validar con el cliente)
- Paciente con **< 4 sesiones completadas** y **> 21 días** sin sesión ni cita futura → `ALTO`.
- **> 14 días** sin sesión ni cita futura → `MEDIO`.
- Un job diario recalcula y dispara push de reenganche.

## Errores
`{ "codigo": "BOX_OCUPADO", "mensaje": "..." }` con HTTP 400/404/409.
