# AppSanaesencia_Grupo8 (DSY1105)

Centralización de servicios terapéuticos, agendamiento y trazabilidad de pacientes para el Centro Integrativo Sanaesencia (Los Vilos). Grupo 8, equipo de 3.

## Stack
- **App**: Android, Kotlin, Jetpack Compose, Material 3, MVVM.
- **Siguientes etapas**: Room, Retrofit y API REST en Spring Boot (`backend/`, contrato en `docs/API_CONTRATO.md`).
- **Entrega**: GitHub, Trello, APK release firmado.
- **Privacidad**: solo datos ficticios (Ley 21.719 y 19.628).

## Estructura
```
app/       Módulo Android (ui, viewmodel, model, repository, navigation)
backend/   Spring Boot (etapa posterior)
docs/      Contrato API y decisiones
```

## Integrantes
| Integrante | Foco inicial |
|---|---|
| 1 | Navegación, estructura base y tema |
| 2 | Pantallas de paciente: catálogo y agendamiento (formularios) |
| 3 | Pantalla adaptable, animaciones/DataStore, perfil con cámara |

## Flujo Git
- `main`: siempre compila. Cambios por Pull Request.
- Ramas: `feature/<tema>` (ej. `feature/home-screen`).
- Primer commit: "Inicio de proyecto + estructura base MVVM".
