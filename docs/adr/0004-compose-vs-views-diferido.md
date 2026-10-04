# 0004 — Compose frente a Views: decisión diferida hasta medir

- **Estado:** Aceptado
- **Fecha:** 2026-10-04

## Contexto

La interfaz de la app puede construirse con Jetpack Compose o con
Views tradicionales. El documento de arranque del proyecto recoge que
Compose es el estándar moderno recomendado por Google. Esa afirmación
no se ha verificado de forma independiente en este proyecto.

Tres factores hacen que la elección no sea obvia en este equipo:

- **Memoria.** Con ~535 MB libres antes de compilar, un build sin
  Compose bajó la memoria libre a un mínimo de 145 MB. Compose añade
  componentes al build; cuánto pesa en esta máquina no se ha medido.
- **Sin IDE.** El ADR 0001 registra que no se usa Android Studio. Se
  asumió que la vista previa de Compose depende del IDE. Es una
  inferencia no verificada.
- **Aislamiento.** El primer proyecto (hola mundo) se hizo sin Compose
  y sin XML, para que un fallo señalara a la herramienta y no a la
  interfaz.

Lo que no se sabe y debe verificarse en la documentación oficial antes
de probar Compose: cómo se declara su compilador junto con el Kotlin
integrado de AGP 9.

## Decisión

No se elige todavía. La decisión se toma al llegar a la fase de
interfaz del mapa de ruta, no antes, y con medición, no por
preferencia.

Hasta entonces el código de interfaz se mantiene mínimo y sin
dependencia de ninguna de las dos opciones. Para que el aplazamiento no
sea una omisión, queda fijado qué se medirá, en un proyecto desechable
con una sola pantalla en cada opción:

- Memoria libre mínima durante `clean assembleDebug`, medida desde una
  segunda ventana de PowerShell.
- Tiempo de `clean assembleDebug` y de un `assembleDebug` incremental.
- Tamaño del APK resultante.
- Si es posible previsualizar o ajustar la interfaz sin IDE, y cuánto
  cuesta cada ciclo de cambio en el dispositivo real.

El resultado se registra en un ADR nuevo que reemplaza a este.

## Alternativas consideradas

- **Elegir Compose ya**, por ser la recomendación de Google. Se
  descartó porque aún no se ha medido su costo en este equipo ni se
  conoce su configuración con AGP 9.
- **Elegir Views ya**, por ser más ligero. Se descartó por la misma
  razón en sentido contrario: sería una suposición sobre el rendimiento
  que no se midió.

## Consecuencias

- La fase de interfaz del mapa de ruta empieza con una medición, no con
  construir.
- Si la medición favorece a Compose pero el costo en este equipo resulta
  alto, será una decisión con datos y no con suposiciones.
- Cualquier código de interfaz escrito antes de decidir se considera
  desechable.
- Este ADR se reemplaza, no se edita, cuando se decida.
