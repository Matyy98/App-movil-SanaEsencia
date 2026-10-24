# Centro Integrativo Sanaesencia - App móvil (Grupo 8)

Aplicación Android para el Centro Integrativo Sanaesencia (Los Vilos). Reúne en un solo lugar el catálogo de servicios terapéuticos, el agendamiento de horas, el seguimiento de pacientes por parte de los terapeutas y la administración del centro (ocupación de boxes, alertas de deserción y promociones).

Asignatura DSY1105 - Desarrollo de Aplicaciones Móviles, Duoc UC. Evaluación Parcial 2.

## Integrantes

| Integrante | Nombre              | Responsabilidad |
|---|---------------------|---|
| 1 | Matias Rodriguez    | Estructura base, tema, navegación, sesión (DataStore), base de datos Room, validaciones, login, registro, inicio adaptable y componentes compartidos |
| 2 | Emiliano Cerda      | Catálogo de servicios, agendamiento en 4 pasos, citas en Room, reglas de horario y boxes |
| 3 | Cristofer Cifuentes | Perfil con cámara y galería, preferencias, profesionales y productos, panel del terapeuta, administración (boxes, alertas y promociones) |

## Funcionalidades

**Acceso**
- Inicio de sesión con validación de correo y contraseña. La sesión queda guardada al cerrar la app.
- Registro de pacientes en 2 pasos (datos personales y contraseña) con validación de cada campo.
- Cada perfil (paciente, terapeuta o administración) entra a su propio inicio y ve su propio menú.

**Paciente**
- Inicio con saludo, accesos rápidos y servicios. Se adapta al celular vertical, horizontal y a tablet.
- Catálogo de servicios con filtros por especialidad y modalidad.
- Profesionales y productos naturales del centro.
- Agendar hora en 4 pasos: servicio; profesional, modalidad y fecha; hora y motivo; confirmación.
- Mis citas: próximas e historial, con opción de cancelar.
- Perfil con foto tomada con la cámara o elegida de la galería, y preferencia de recordatorios.

**Terapeuta**
- Mis pacientes, con su nivel de riesgo y su agenda. Puede marcar las sesiones como realizadas o canceladas.

**Administración**
- Panel con la ocupación de los 8 boxes por día.
- Alertas de pacientes con riesgo de abandonar su tratamiento.
- Creación de promociones con formulario validado.

**General**
- Menú lateral, barra superior y barra inferior según el perfil.
- Listas por páginas y mensajes animados de error y de éxito.
- Datos guardados en el teléfono con Room (usuarios, citas y promociones) y DataStore (sesión y preferencias).

## Tecnologías

- Kotlin y Jetpack Compose con Material 3
- Arquitectura MVVM (ViewModel, StateFlow y estado de pantalla con UiState)
- Navigation Compose con navegación centralizada en un MainViewModel
- Diseño adaptable con WindowSizeClass (compacto, mediano y expandido)
- Room para la base de datos local y DataStore Preferences para la sesión
- Cámara y galería con permiso en tiempo de ejecución

## Cómo ejecutar

1. Clonar el repositorio:
   `git clone https://github.com/SanaEsencia/App-movil-SanaEsencia.git`
2. Abrir la carpeta del proyecto en Android Studio y esperar que termine la sincronización de Gradle.
3. Elegir un emulador o un celular con Android 13 o superior (minSdk 33).
4. Presionar Run.

## Cuentas de prueba

| Perfil | Correo | Contraseña |
|---|---|---|
| Paciente | paciente@sanaesencia.cl | Paciente123 |
| Terapeuta | terapeuta@sanaesencia.cl | Terapeuta123 |
| Administración | admin@sanaesencia.cl | Admin1234 |

También se puede crear una cuenta de paciente nueva desde "Regístrate".

## Estructura

```
app/src/main/java/cl/sanaesencia/app/
  data/         Room (entidades, DAOs y base de datos) y DataStore
  logic/        Validaciones y reglas de negocio (fechas, boxes, deserción)
  model/        Modelos de datos y estados de pantalla
  repository/   Acceso a los datos
  viewmodel/    ViewModels de cada parte de la app
  navigation/   Rutas, eventos de navegación y AppNavigation
  ui/           Tema, componentes compartidos y pantallas por perfil
```

## Privacidad

La app usa solo datos ficticios, de acuerdo con la Ley 19.628 y la Ley 21.719 sobre protección de datos personales.