# 0008 — CI en GitHub Actions: un flujo, el mismo comando que el hook

- **Estado:** Aceptado
- **Fecha:** 2026-10-07

## Contexto

El ADR 0005 (pieza 4) pide una CI que ejecute lo mismo que los controles
locales en cada push, porque el servidor es la autoridad y los hooks
pueden saltarse.

Versiones de las acciones, de fuentes consultadas en octubre de 2026:
`actions/checkout` v7 y `actions/setup-java` v6 (README de setup-java y
actualizaciones de Dependabot) y `gradle/actions/setup-gradle` v6 (la
documentación de Gradle la recomienda para proyectos nuevos). La página
de GitHub Docs que encontré estaba desfasada y no se usó para versiones.

Un paso de diagnóstico temporal mostró qué trae el servidor
(`ubuntu-latest`): SDK de Android en `/usr/local/lib/android/sdk` con
build-tools 36.0.0 y platform android-36 (entre otras), Temurin
17.0.20.1, 15.989 MB de RAM y 4 núcleos.

## Decisión

Un flujo, `.github/workflows/ci.yml`:
- Se ejecuta en cada push a cualquier rama y a mano (`workflow_dispatch`).
- Permisos mínimos: `contents: read`. Tope de 30 minutos.
- Pasos: checkout, Temurin 17, `setup-gradle` con
  `cache-provider: basic` y `./gradlew check`, el mismo comando y el
  mismo script que el hook `pre-push`.
- No instala el SDK ni acepta licencias: el servidor trae lo necesario.
- La caché es la básica (MIT). La mejorada viene por defecto y es un
  componente propietario con términos de uso propios (gratuito para
  repositorios públicos, según Gradle). Para un molde se prefiere no
  introducir un componente cerrado. Es reversible con una línea.

Detalle respecto al ADR 0006: la propiedad `ignore` del bloque `lint`
está obsoleta ("ignore y disable son sinónimos"); las dos reglas
silenciadas usan ahora `disable +=`.

## Evidencia de que funciona

- Verde (ejecución 37537327566): `check` terminó con éxito sobre una
  máquina limpia, con las 30 tareas ejecutadas, en 1 m 26 s. El
  baseline de lint funcionó en Linux ("no new issues, 3 filtered by
  baseline"), `gradlew` ejecutó con su permiso y fin de línea, y
  `setup-gradle` validó el jar del wrapper ("All Gradle Wrapper jars
  are valid").
- Rojo (ejecución 37544094528): un archivo con una ruta "/sdcard/..."
  hizo fallar `check` con `SdCardPath` y la ejecución terminó en fallo.
  El `pre-push` local bloqueó ese mismo cambio; solo `--no-verify` lo
  llevó al servidor.
- Sin avisos tras `disable` (ejecución 37563073702): desaparecieron los
  avisos de obsolescencia del script de build.

## Alternativas consideradas

- **Caché mejorada** (por defecto). Descartada por ser propietaria.
- **Una acción de terceros para instalar el SDK.** No hizo falta; no se
  evaluó.
- **Acciones fijadas por hash de commit.** No se hizo: las etiquetas
  pueden reapuntarse. Queda como deuda de cadena de suministro.
- **Disparar solo con `pull_request`.** Se pospone con la decisión de
  proteger `main`.

## Consecuencias

- El servidor no reproduce las limitaciones de memoria: 15.989 MB frente
  a ~3,4 GB. También difieren el parche de JDK (17.0.20.1 frente a
  17.0.19) y las plataformas del SDK (el servidor trae 37.x; el equipo
  local, solo 34 y 36). Esas diferencias son reales.
- Con cero pruebas, la CI también sale en verde (`NO-SOURCE`): la brecha
  del ADR 0005 se reproduce en el servidor.
- `setup-gradle` solo escribe la caché desde la rama por defecto: en
  ramas la caché parte de vacío (86 y 88 s por ejecución). El efecto de
  la caché no se ha medido.
- Las ejecuciones son visibles en un repositorio público, incluida la
  roja de la prueba.
- No se probó: un checksum de distribución erróneo, la protección de
  `main`, ni el comportamiento de la CI ante un fallo de red.
