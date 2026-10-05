# 0007 — Controles locales: pre-commit rápido y pre-push con check

- **Estado:** Aceptado
- **Fecha:** 2026-10-05

## Contexto

El ADR 0005 pide controles locales que den retroalimentación sin
esperar al servidor, con una condición: un control que tarde tanto que
se acabe saltando debilita el escudo, así que su contenido se decide
midiendo.

Mediciones propias, en este equipo (2 núcleos, ~3,4 GB de RAM usables):
- `gradlew check` desde un daemon nuevo con la caché de tareas vacía:
  164 s, con un mínimo de 30 MB de memoria libre. Con la caché llena,
  95 a 98 s. Con el daemon caliente: de 4 a 32 s según cuánto haya
  cambiado.
- Arrancar `./gradlew --version` desde el `sh` de Git: 1,6 a 2,2 s,
  frente a 1,2 a 1,5 s con `gradlew.bat`. Una primera medición dio
  31 s; fue un arranque en frío de una sola vez y su causa no se
  determinó.

Experimentos en un repositorio desechable con Git 2.51.2 (PortableGit)
en Windows 11: los hooks escritos en `sh` se ejecutan; `core.hooksPath`
acepta una ruta relativa; un código de salida distinto de 0 aborta el
commit o el push; `pre-commit` también se ejecuta con
`commit --amend`; `--no-verify` los salta.

## Decisión

- Los hooks viven en `.githooks/`, versionados. `.gitattributes` fuerza
  fin de línea LF en esa carpeta y los scripts llevan permiso de
  ejecución en el índice.
- **`pre-commit`**: solo `git diff --cached --check` (espacios
  sobrantes y marcas de conflicto en lo preparado). Tarda segundos. El
  lint y las pruebas no entran: su costo medido lo haría saltarse.
- **`pre-push`**: `./gradlew check` (pruebas y lint), el mismo script
  que usará la CI. Avisa si hay cambios sin commitear. Se acepta un
  peor caso de unos 100 s con el daemon frío.
- **Activación**: cada clon debe ejecutar una vez
  `git config core.hooksPath .githooks`. Esa configuración es local y
  no viaja con el repositorio.

## Evidencia de que funciona

- `pre-commit` con un archivo con espacios sobrantes preparado: salió
  con código 1 y no se creó el commit.
- `pre-push` con `check` en verde: código 0 y la rama llegó a un remoto
  local desechable.
- `pre-push` con un aviso de lint nuevo (una ruta "/sdcard/..." en un
  archivo): `check` falló, el hook salió con código 1 y la rama no
  llegó al remoto.

## Alternativas consideradas

- **Que `pre-commit` ejecute `check`.** Se descartó: de 95 a 164 s en
  frío por commit se saltaría.
- **Sin hooks, solo CI.** Se descartó: el ADR 0005 pide retroalimentación
  local.
- **Hooks en `.git/hooks`.** Se descartó: esa carpeta no se versiona.
- **`gradlew.bat` en el hook.** Se descartó: no es el script que usará
  la CI, y el ahorro medido es de ~0,5 s.
- **Activación automática** (por ejemplo, desde una tarea de Gradle). No
  se evaluó; queda como pregunta abierta.

## Consecuencias

- Los hooks se pueden saltar con `--no-verify`, y un clon nuevo nace sin
  ellos hasta ejecutar el comando de activación. Son un aviso; la CI es
  la autoridad.
- `pre-push` valida el directorio de trabajo, no los commits que se
  suben: en una prueba bloqueó un push de un commit limpio por un
  archivo sin commitear. La CI validará lo publicado.
- Un cambio que solo toque documentación también paga el `check`
  completo. Se acepta por simplicidad.
- No se verificó: el comportamiento de los hooks en Linux o macOS, si
  `pre-push` corre cuando no hay nada que subir, ni la memoria
  consumida durante el hook.
- Si el peor caso de ~100 s lleva a usar `--no-verify` con frecuencia,
  se reabre este ADR con esa experiencia.
