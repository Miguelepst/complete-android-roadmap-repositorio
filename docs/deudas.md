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
4. CI en GitHub ejecutando el mismo comando, y protección de la rama
   `main` si el plan del repositorio la permite. Considerar un control
   que falle si el baseline crece (idea sin verificar).
**Pregunta abierta:** con cero pruebas, `check` también sale en verde y
con código 0 (comprobado). Solo cambia que no se genera la carpeta de
resultados de pruebas. Cómo evitar que un verde sin pruebas pase
inadvertido debe resolverse con la primera lógica, cuando haya algo
real que contar. Una idea, sin verificar: exigir que exista al menos
un resultado de prueba.

## ABIERTAS

Orden LIFO: la más reciente arriba.

### ADR de cadena de suministro y secretos
Pendiente de registrar en un ADR: checksum de la distribución de Gradle
fijado en el wrapper, validación del wrapper en la CI (a verificar con
la versión actual de la acción), alertas de secret scanning y push
protection activadas en GitHub, y la revisión del historial publicado
sin hallazgos. El bloqueo de la push protection no se ha probado.
**Cuándo:** junto con la CI (pieza 4).

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
### Licencias del SDK sin verificar en una máquina limpia
En el equipo de desarrollo las licencias del SDK ya estaban aceptadas,
así que nunca se vio ese paso. En una máquina nueva, `sdkmanager
--licenses` puede ser necesario y el README no lo cubre.
**Cuándo:** al configurar el CI (paso 2), que parte de una máquina
limpia.

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
