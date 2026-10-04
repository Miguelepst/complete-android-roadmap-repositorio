# 0003 — Caché de Gradle fuera del disco C:, en D:\gradle-home

- **Estado:** Aceptado
- **Fecha:** 2026-10-04

## Contexto

Gradle guarda por defecto su caché (distribuciones, dependencias,
artefactos de compilación) en `C:\Users\<usuario>\.gradle`. Esa carpeta
ya pesaba 2,32 GB antes de este proyecto, por otros proyectos del
autor, y el disco C: tenía 30,1 GB libres. El disco D: tenía 425,6 GB
libres.

Un proyecto Android con AGP, Kotlin, Room y otras dependencias suma
varios GB más a esa caché. La cifra exacta no se midió: es una
estimación razonada, no un dato.

No existía configuración global de Gradle en la carpeta por defecto
(ni `gradle.properties`, ni `init.gradle`, ni `init.d`), así que cambiar
la ubicación no pierde ningún ajuste previo.

## Decisión

Se define la variable de entorno de usuario `GRADLE_USER_HOME` con el
valor `D:\gradle-home`. La carpeta está fuera de cualquier repositorio
(si estuviera dentro, Git intentaría versionar gigabytes de caché) y su
ruta no tiene espacios ni acentos.

La variable vive en el entorno de la máquina, no en el repositorio.

## Evidencia de que funciona

La primera descarga de Gradle 9.5.0 quedó en
`D:\gradle-home\wrapper\dists\gradle-9.5.0-bin`, y el build del
proyecto y el de un clon limpio se ejecutaron con esa configuración.

## Consecuencias

- Los demás proyectos Gradle del autor (`reproductor-media3`,
  `telegram`) dejan de ver su caché actual en C: y, previsiblemente,
  descargarán sus dependencias de nuevo en su próxima compilación. Esto
  no se ha comprobado: esos proyectos no se compilaron después del
  cambio.
- Los 2,32 GB existentes en `C:\Users\<usuario>\.gradle` no se
  eliminaron. Limpiarlos queda como decisión pendiente del autor.
- Es una configuración de la máquina: un equipo nuevo debe repetirla.
  Se documenta en el README como paso opcional de preparación del
  entorno. Un CI no la necesita.
- La variable solo la ven las terminales abiertas después de
  definirla.
- Revertir es reversible: eliminar la variable devuelve el
  comportamiento por defecto.
