# 0006 — Lint estricto con baseline y silencios explícitos

- **Estado:** Aceptado
- **Fecha:** 2026-10-04

## Contexto

El ADR 0005 exige que `gradlew lint` forme parte de la puerta de
calidad. El lint ya corría dentro de `gradlew check`, pero solo
informaba: encontró cinco hallazgos, todos de severidad `warning`, y
`check` salió en verde. Según la documentación oficial, `abortOnError`
detiene el build solo ante errores, no ante avisos.

Los cinco hallazgos eran:
- `AndroidGradlePluginVersion` (existe Gradle 9.8.0) y
  `GradleDependency` (existe compileSdk 37): avisos de "hay una versión
  más nueva".
- `OldTargetApi`: existe una API más nueva que `targetSdk 36`.
- `MissingApplicationIcon` y `SetTextI18n`: deudas reales del hola mundo
  (sin icono, texto fijo en código).

Con `warningsAsErrors` activado y sin más cambios, un build que pasa
hoy podría fallar mañana porque salió una versión nueva, sin que se
toque el código. Eso contradice la decisión de fijar versiones (ADR
0002) y llevaría a saltarse la puerta.

## Decisión

En `app/build.gradle.kts`, dentro de `android { lint { ... } }`:

- `warningsAsErrors = true`: cualquier aviso nuevo cierra la puerta.
- `ignore += "AndroidGradlePluginVersion"` y
  `ignore += "GradleDependency"`, con el motivo escrito como comentario
  junto a las reglas: avisan del paso del tiempo y no de un cambio
  nuestro, y las versiones se fijan a propósito.
- `baseline = file("lint-baseline.xml")`, generado con
  `gradlew updateLintBaseline`, con tres entradas: `OldTargetApi`,
  `MissingApplicationIcon` y `SetTextI18n`. Son deudas reales,
  registradas en `docs/deudas.md`.

`OldTargetApi` no se silencia: podría ocultar un requisito real de
Google Play. Queda en el baseline, visible y versionado.

**Regla del baseline:** solo puede encogerse. Una entrada se retira
del archivo cuando se resuelve la deuda que representa. Un cambio que
haga crecer el baseline se rechaza en la revisión del diff.

## Evidencia de que funciona

- Con `warningsAsErrors = true` y sin baseline: `check` falló con
  código de salida 1 y "Lint found 5 errors".
- Con baseline y los dos silencios: `check` en verde, código 0, y lint
  informó "no new issues (and 3 errors filtered by baseline)".
- Con un archivo desechable que escribía una ruta fija "/sdcard/...":
  `check` volvió a fallar, código 1, con "1 error (and 3 errors
  filtered by baseline)" y la regla `SdCardPath`. Tras borrarlo, volvió
  a verde.
- El baseline no contiene rutas absolutas de la máquina de desarrollo.

## Alternativas consideradas

- **Dejar el lint informativo.** Es lo que ocurría: la puerta nunca se
  cierra.
- **Elevar solo reglas concretas a error.** Más fino, pero obliga a
  mantener una lista, y las reglas nuevas que traiga cada versión de
  lint quedarían fuera.
- **Silenciar también `OldTargetApi`.** Se descartó por el riesgo de
  esconder un requisito de publicación.
- **Silenciar con un XML de `lintConfig`.** Se descartó: la DSL permite
  hacerlo en `build.gradle.kts`, con el motivo junto a la regla y un
  archivo menos.

## Consecuencias

- El baseline puede regenerarse con `updateLintBaseline`, y eso
  absorbería avisos nuevos sin que `check` se queje. La regla de que
  "solo encoge" depende de revisar el diff; no hay un control
  automático. Podría añadirse en la CI.
- Al actualizar AGP, las reglas nuevas de lint pueden hacer fallar
  `check` sin cambios nuestros. Es deseable y a la vez un costo.
- No se comprobó si el baseline sigue absorbiendo un aviso cuando el
  código se desplaza de línea, ni si absorbe `OldTargetApi` cuando
  exista una API más nueva que la actual.
- Medido en este equipo: `check` desde un daemon nuevo tardó 164 s y
  dejó 30 MB de memoria libre como mínimo; con el daemon caliente, 17 a
  32 s. El lint completo no es apto para pre-commit.
