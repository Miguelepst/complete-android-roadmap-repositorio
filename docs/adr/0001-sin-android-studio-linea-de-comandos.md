# 0001 — Desarrollo Android sin Android Studio, por línea de comandos

- **Estado:** Aceptado
- **Fecha:** 2026-10-04

## Contexto

El equipo de desarrollo es un Lenovo 81NB con AMD Ryzen 3 3200U
(2 núcleos físicos), 3,44 GB de RAM usables y sin pantalla táctil.

La experiencia directa del autor con Android Studio en esta máquina es
que resulta inutilizable: esperas largas para que terminen los
procesos, bloqueos y lentitud general que rompen el flujo de trabajo.
Esta es la razón principal de la decisión. Es una limitación de
hardware, no una preferencia.

Como dato de contexto medido (no como prueba): una compilación por
línea de comandos, sin ningún IDE abierto, hizo bajar la memoria libre
a un mínimo de 145 MB, y el primer build de un clon limpio tardó
215,8 s.

## Decisión

No se usa Android Studio. Todo el flujo se hace por línea de comandos:
compilación y pruebas con `gradlew`, instalación y depuración con
`adb`, administración del SDK con `sdkmanager`. El editor es CudaText.
Se prueba en un dispositivo físico (Android 11), no en emulador, por la
misma limitación de memoria.

## Consecuencias

**Se pierde:**
- Autocompletado y refactorización asistida de Kotlin: CudaText no
  tiene inteligencia sobre el lenguaje.
- La vista previa de Jetpack Compose, que se asume que depende del IDE (no verificado, ver ADR 0004). Esto afecta
  directamente la decisión pendiente Compose frente a Views, que se
  tomará en otro ADR con esta pérdida como factor.
- El depurador gráfico: se depura con `adb logcat` y mensajes de
  registro.
- Los avisos de lint en el editor: el lint se corre desde `gradlew`.

**Se gana:**
- El proyecto no depende de ningún IDE: compila igual en esta máquina
  y en un CI.
- Un flujo reproducible y documentado paso a paso.

**Riesgo:** sin refactorización asistida, los cambios grandes de
estructura cuestan más y son más propensos a errores. Las pruebas
automatizadas desde el primer paso (Shift Left) son la mitigación.
