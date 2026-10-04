# 0002 — AGP 9.3.3 con Gradle 9.5.0 y Kotlin integrado

- **Estado:** Aceptado
- **Fecha:** 2026-10-04

## Contexto

El proyecto necesita fijar la versión del Android Gradle Plugin (AGP)
y de Gradle antes de escribir código. Antes de fijarlas se leyó la
documentación oficial de AGP 9.3, que establece: Gradle 9.5.0 como
mínimo (y recomendado), JDK 17 como mínimo, build-tools 36.0.0 como
mínimo y API 37 como máxima soportada.

Las herramientas instaladas en la máquina cumplen esos requisitos
(JDK 17.0.19, build-tools 36.0.0, platform android-36). Los wrappers de
otros proyectos del autor usan Gradle 8.7 y 8.11.1, que AGP 9.3 no
acepta.

AGP 9.0 y posteriores traen soporte de Kotlin integrado, activado por
defecto. Según la documentación oficial, aplicar además el plugin
`org.jetbrains.kotlin.android` hace fallar el build.

## Decisión

- AGP **9.3.3**, última corrección de la línea 9.3 listada en las notas
  de versión oficiales.
- Gradle **9.5.0**, el emparejamiento documentado por Google.
- **No se declara ningún plugin de Kotlin.** Se usa el Kotlin integrado
  de AGP.

## Evidencia de que funciona

- AGP 9.3.3 se resolvió y se descargó sin error.
- Gradle 9.5.0 arrancó sobre JDK 17.0.19 desde un
  `gradle-wrapper.jar` de la época de Gradle 8.7.
- El compilador integrado generó `MainActivity.class` desde nuestro
  Kotlin; la caché de dependencias contiene un único compilador de
  Kotlin, versión 2.2.10, que es la que AGP trajo por su cuenta.
- Un clon limpio del repositorio compiló y produjo el APK.

## Alternativas consideradas

- **AGP 8.x con Gradle 8.x y el plugin `kotlin-android`.** No se probó.
  Se descartó por criterio y no por evidencia propia: iniciar un
  proyecto nuevo en una línea anterior obligaría a migrar más adelante,
  y la documentación oficial ya describe esa migración como el camino.
- **Gradle más nuevo que 9.5.0.** Existen versiones 9.6.x, pero la
  documentación de AGP 9.3 empareja 9.5.0, y no se contrastó que las
  posteriores estén soportadas por esta línea de AGP.

## Consecuencias

- `kapt` es incompatible con el Kotlin integrado. Cuando se agregue
  Room se usará KSP.
- La versión de Kotlin la determina AGP y no se fija en este proyecto.
  Actualizar AGP puede cambiar la versión de Kotlin sin avisar.
- El `gradle-wrapper.jar` sigue siendo de la época de Gradle 8.7. Debe
  regenerarse para que los cuatro archivos del wrapper tengan la misma
  versión. Pendiente en `deudas.md`.
- Estas versiones envejecerán. Cuando cambien, no se edita este ADR:
  se escribe uno nuevo que lo reemplace.
