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
1. Ejecución de pruebas con `gradlew` (`gradlew check`).
2. `gradlew lint`.
3. Controles locales (hooks versionados), con tiempos medidos.
4. CI en GitHub ejecutando el mismo comando, y protección de la rama
   `main` si el plan del repositorio la permite.
**Pregunta abierta:** sin lógica no hay pruebas reales que escribir
(el ADR 0005 descarta pruebas vacías). Cómo comprobar que la ejecución
de pruebas funciona sin inventar una prueba falsa debe decidirse al
llegar a la pieza 1.

## ABIERTAS

Orden LIFO: la más reciente arriba.

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

### `gradle-wrapper.jar` de la época de Gradle 8.7
Funciona con Gradle 9.5.0 (verificado), pero los cuatro archivos del
wrapper no tienen la misma versión.
**Cuándo:** pronto, con `gradlew wrapper`.

## RESUELTAS

### `ANDROID_SDK_ROOT` vacío
Gradle encontró el SDK solo con `ANDROID_HOME`; no hizo falta
definirla. Verificado en el primer build.

### Dos `adb.exe` distintos
El del `PATH` es el 34.0.5 de `scrcpy`; el del SDK es el 37.0.0. Se
alternaron sin reinicios del servidor, y se decidió no tocar el `PATH`
para no afectar a `scrcpy`. Si apareciera un fallo real, se reabre con
evidencia.
