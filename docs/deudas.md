# Deudas

Solo deudas abiertas. Al resolverse se retiran (si la solución queda en
el código o en el historial) o pasan a RESUELTAS (si la solución vive
fuera del repositorio).

## ANCLADAS

Una deuda se ancla solo si **bloquea** algo concreto, y debe decir qué
bloquea y cuándo se desancla. "Es importante" no basta. Máximo tres:
para anclar una cuarta, se revisa si alguna de las actuales ya no
bloquea. Una deuda vive en un solo lugar: al anclarse o desanclarse se
mueve, no se copia.

### Puerta de calidad antes de lógica (ADR 0005)
**Bloquea:** escribir cualquier lógica de dominio.
**Se desancla cuando:** la CI se haya visto en verde.
Cuatro piezas, cada una verificada antes de empezar la siguiente:
1. **HECHA.** Ejecución de pruebas con `gradlew check` (JUnit 4.13.2).
   Verificada con una prueba desechable que no se commiteó: con un
   fallo, `check` termina en rojo con código de salida 1; con la
   prueba corregida, en verde con código 0 y el informe XML registra
   la prueba ejecutada.
2. **HECHA.** Lint estricto (ADR 0006): avisos como errores, dos reglas
   de "versión más nueva" silenciadas con motivo escrito y un baseline
   de tres entradas que solo puede encogerse. Verificado en rojo, en
   verde y con un aviso nuevo que sí falla.
3. **HECHA.** Controles locales (ADR 0007): `.githooks/pre-commit`
   (espacios y marcas de conflicto) y `.githooks/pre-push`
   (`./gradlew check`), probados tanto abortando como dejando pasar. Se
   activan por clon con `git config core.hooksPath .githooks`.
4. **HECHA EN RAMA.** CI en GitHub (ADR 0008), verificada en verde y en
   rojo. Pendiente: integrarla a `main` y verla en verde allí; proteger
   `main` si el plan lo permite (decisión de flujo, probablemente por
   pull request); un control que falle si el baseline crece (idea sin
   verificar). Al verla en verde en `main`, esta deuda se retira y la
   pregunta abierta pasa a ser una deuda abierta.
**Pregunta abierta:** con cero pruebas, `check` también sale en verde y
con código 0 (comprobado). Solo cambia que no se genera la carpeta de
resultados de pruebas. Cómo evitar que un verde sin pruebas pase
inadvertido debe resolverse con la primera lógica, cuando haya algo
real que contar. Una idea, sin verificar: exigir que exista al menos
un resultado de prueba.

## ABIERTAS

Orden LIFO: la más reciente arriba.

### Medir el efecto de la caché de la CI
`setup-gradle` solo escribe la caché desde la rama por defecto: en
`ci-prueba` las ejecuciones parten de caché vacía (86 y 88 s). Tras
integrar a `main`, comparar la primera ejecución (escribe) con la
siguiente (lee).
**Cuándo:** tras integrar a `main`.

### Revisión del pre-push (ADR 0007)
Se ejecuta también al borrar ramas remotas (`git push --delete` pagó un
`check` completo de 29 s). Con el daemon frío costó entre 98 y 164 s en
cinco mediciones, por encima del peor caso de ~100 s del ADR 0007; el
texto del hook dice "unos 2 min". Si lleva a usar `--no-verify` con
frecuencia, se reabre ese ADR con una decisión nueva.
**Cuándo:** al revisar la experiencia de uso.

### ADR de cadena de suministro y secretos
Pendiente de registrar en un ADR: checksum de la distribución de Gradle
fijado en el wrapper; validación del wrapper en la CI (verificada:
`setup-gradle@v6` informa que los jar son válidos); fijar las acciones
de la CI por hash de commit en lugar de etiqueta (hoy no); alertas de
secret scanning y push protection activadas en GitHub; y la revisión del
historial publicado sin hallazgos. El bloqueo de la push protection y el
rechazo de un checksum erróneo no se han probado.
**Cuándo:** tras integrar la CI a `main`.

### Activación automática de los hooks en un clon nuevo
`core.hooksPath` es configuración local de cada clon: hoy depende de
ejecutar un comando una vez (README). No se ha verificado ninguna forma
de automatizarlo, por ejemplo con una tarea de Gradle.
**Cuándo:** antes de usar este repositorio como molde (ADR 0005, "Para
quien copie este proyecto").

### Requisito de `targetSdk` de Google Play sin verificar
El lint avisa de que existe una API más nueva que 36 (`OldTargetApi`,
en el baseline). Subir `targetSdk` exigiría instalar
`platforms;android-37`, y no se ha verificado qué exige Google Play al
publicar.
**Cuándo:** al preparar la publicación.

### Icono de la aplicación
El lint avisa de que no hay `android:icon` (`MissingApplicationIcon`,
en el baseline).
**Cuándo:** antes de publicar.

### Textos de interfaz en recursos
El lint avisa de texto fijo en `setText` (`SetTextI18n`, en el
baseline). Los textos de interfaz irán en recursos de cadena desde el
primer texto real. Qué idiomas se traducen es una decisión de producto
aparte.
**Cuándo:** fase de interfaz.
### Licencias del SDK en un equipo de desarrollo nuevo
La CI no las necesita: el servidor trae el SDK con build-tools 36.0.0 y
platform 36, y `check` pasó sin aceptar licencias (ADR 0008). En un
equipo nuevo, `sdkmanager --licenses` puede ser necesario y el README no
lo cubre; tampoco se verificó qué pasa si se necesita un componente que
el SDK no tenga instalado.
**Cuándo:** antes de usar este repositorio como molde.

### Limpieza de la caché antigua de Gradle en C:
`C:\Users\<usuario>\.gradle` conserva 2,32 GB de proyectos anteriores
(ADR 0003). Es decisión del autor si se borra.
**Cuándo:** sin urgencia; C: tenía 30,1 GB libres.

### Valores de memoria de Gradle provisionales
`-Xmx768m` y `MaxMetaspaceSize=512m` salen de una sola sesión. Con
256 MB de Metaspace el daemon murió; con 512 MB no se repitió, pero
fueron pocas ejecuciones. Durante el build la memoria libre del equipo
bajó a 145 MB.
**Cuándo:** revisar al agregar Room y al medir Compose frente a Views
(ADR 0004), que pesarán más. Reabrir de inmediato si reaparece un
daemon `STOPPED (after running out of ...)`.

### Room requiere KSP, no kapt
El Kotlin integrado de AGP 9 es incompatible con `kapt` (ADR 0002).
**Cuándo:** al agregar persistencia.

### `minSdk = 30` es provisional
Coincide con el dispositivo de pruebas; no se ha decidido qué rango de
dispositivos cubrir.
**Cuándo:** ADR del paso 2.

### `applicationId = com.example.vocabulario` es un marcador
Google Play no acepta identificadores que empiecen por `com.example`.
**Cuándo:** antes de publicar.

## RESUELTAS

### `ANDROID_SDK_ROOT` vacío
Gradle encontró el SDK solo con `ANDROID_HOME`; no hizo falta
definirla. Verificado en el primer build.

### Dos `adb.exe` distintos
El del `PATH` es el 34.0.5 de `scrcpy`; el del SDK es el 37.0.0. Se
alternaron sin reinicios del servidor, y se decidió no tocar el `PATH`
para no afectar a `scrcpy`. Si apareciera un fallo real, se reabre con
evidencia.
