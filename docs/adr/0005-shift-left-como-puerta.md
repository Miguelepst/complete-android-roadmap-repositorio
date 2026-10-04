# 0005 — Shift Left como puerta: calidad automatizada antes de lógica

- **Estado:** Aceptado
- **Fecha:** 2026-10-04

## Contexto

Al iniciar el proyecto se acordó el enfoque Shift Left: pruebas
automatizadas e integración continua (CI) desde el primer paso, no
agregadas a mitad de camino.

El primer commit (5aea8af) no incluyó ni pruebas ni CI. No hubo
negligencia: el acuerdo era ambiguo. El documento de arranque decía
"desde el primer paso del proyecto" en una sección y ubicaba "CI desde
el día 1" dentro de la fase de arquitectura base en otra. Nadie fijó
qué cuenta como "primer paso".

La omisión se detectó antes de escribir lógica de dominio, por lo que
no hay código que retroadaptar. La detectó el autor al preguntar, no un
control automático. Eso es justo lo que no se puede esperar que ocurra
en cada proyecto.

## Decisión

**Regla (la puerta):** no se escribe lógica de dominio hasta que
existan, en verde, (a) ejecución de pruebas mediante `gradlew`, (b)
`gradlew lint`, (c) los controles locales descritos abajo y (d) una CI
que ejecute lo mismo en cada push.

**Definición precisa de "primer paso":** es el momento anterior al
primer commit que contenga lógica de dominio, no anterior a cualquier
commit. Un hola mundo que solo verifica el entorno no tiene lógica que
proteger y está exento. La exención queda escrita aquí para que no sea
tácita.

**Primera lógica:** cada pieza de lógica llega en el mismo commit que
sus pruebas.

**Un solo punto de entrada:** local y servidor ejecutan el mismo
comando (`gradlew check`). Las versiones las fijan el wrapper de Gradle
y JDK 17. Así la CI no puede divergir de lo que se ve en local.

**Dos capas con roles distintos:**
- Local (hooks de Git, versionados en el repositorio): retroalimentación
  rápida sin esperar al servidor. Es un aviso: puede saltarse.
- Servidor (CI + protección de la rama `main`): la autoridad. Se
  intentará que GitHub exija la CI en verde antes de aceptar cambios.
  Si no está disponible para este repositorio, se declarará en un ADR
  nuevo y hasta entonces la regla depende de disciplina, dicho
  abiertamente.

**Qué corre en cada hook se decide midiendo**, no por preferencia: un
control que tarde tanto que se acabe saltando debilita el escudo. El
criterio es que el hook de commit tarde segundos.

**Orden de introducción:** pruebas con `gradlew`, lint, controles
locales, CI, protección de rama. Otros controles se añaden cuando exista
algo que proteger. Cada pieza se verifica antes de añadir la siguiente.

## Para quien copie este proyecto

El orden correcto es entorno, hola mundo de verificación,
infraestructura de calidad y recién después lógica. Este repositorio
empezó con un paso fuera de ese orden y lo documenta: el historial
muestra el primer commit sin pruebas ni CI, y este ADR explica por qué
no debe repetirse. No empieces por la lógica.

## Límites conocidos del espejo local

- Los hooks locales pueden saltarse (`--no-verify`) y por defecto no
  se versionan; un clon nuevo nace sin ellos. Se versionan en una
  carpeta del repositorio, pero activarlos en cada clon requiere un
  paso manual. Automatizarlo es una pregunta abierta, no verificada.
- Local (Windows) y servidor (probablemente Linux) no son idénticos. Es
  deseable: ya detectó diferencias reales (fin de línea y permiso de
  `gradlew`).
- La versión exacta del parche de JDK puede diferir entre local y
  servidor.

## Alternativas consideradas

- **Reescribir el primer commit (amend) para incluir CI.** Se
  descartó: el commit ya estaba publicado en GitHub, y reescribir
  historia compartida está prohibido por el método del proyecto.
  Además, una CI nunca vista en verde dentro de un commit rompe la
  regla de que cada paso quede verificado.
- **Escribir pruebas del hola mundo.** Se descartó: no hay lógica que
  probar, y una prueba que no verifica nada da falsa seguridad y es mal
  ejemplo.
- **Dejar la omisión solo anotada en `deudas.md`.** Se descartó: una
  nota en una lista no obliga a nada.

## Consecuencias

- La primera funcionalidad visible tarda más, porque la infraestructura
  de calidad va primero.
- Las pruebas y la CI no detectan errores de diseño. Reducen el costo
  de los errores de código, no sustituyen el criterio.
- La regla puede saltarse a propósito. El mecanismo de protección de
  rama cambia el modo de fallo de "se me olvidó" a "tuve que saltarme un
  control", pero no lo elimina.
- Una CI consume memoria y tiempo del servidor; su costo real en este
  proyecto no se ha medido.
- Un control local lento se salta. Su velocidad es parte de su diseño.
- La CI y los hooks comparten comando, pero no entorno.
- El cumplimiento de este ADR no está demostrado todavía. Se verifica
  con la deuda correspondiente en `deudas.md` y se da por cumplido
  cuando la CI exista y se haya visto en verde.
