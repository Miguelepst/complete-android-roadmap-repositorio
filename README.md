# Vocabulario

App Android de tarjetas de vocabulario de inglés con repetición
espaciada. Es un proyecto de aprendizaje de arquitectura y prácticas
profesionales de desarrollo, documentado paso a paso.

**Estado:** hola mundo. Una Activity que muestra un texto. Compila e
instala en un dispositivo real; todavía no tiene funcionalidad de
dominio, pruebas automatizadas ni integración continua.

## Orden de arranque

Si usas este repositorio como molde, el orden es: entorno, hola mundo
que verifica el entorno, infraestructura de calidad (pruebas, lint,
controles locales, CI) y solo después lógica de dominio.

Este repositorio no lo cumplió del todo: su primer commit no incluyó
nada de esa infraestructura (se ve en el historial). Queda documentado,
junto con la regla que lo evita, en
`docs/adr/0005-shift-left-como-puerta.md`. No empieces por la lógica.

## Requisitos

Verificado en Windows 11 (64 bits) con PowerShell:

- **JDK 17** (probado con Temurin 17.0.19), con `JAVA_HOME`
  apuntando a él.
- **Android SDK** con `ANDROID_HOME` apuntando a su carpeta, e
  instalados `build-tools;36.0.0` y `platforms;android-36`.
- Un dispositivo con Android 11 o superior con la depuración USB
  activada (`minSdk` es 30; es un valor provisional).

No hace falta instalar Gradle: el proyecto incluye su *wrapper*, que
descarga la versión exacta (9.5.0) en la primera ejecución.

No se ha verificado: Linux, macOS, el uso de emulador, ni el aceptar
las licencias del SDK en una máquina donde no estén ya aceptadas.

## Compilar

```powershell
.\gradlew.bat assembleDebug
```

El APK queda en `app\build\outputs\apk\debug\app-debug.apk`.

Tiempos medidos en el equipo de desarrollo (Ryzen 3 3200U, 3,4 GB de
RAM usables):

| Ejecución | Tiempo |
|---|---|
| Primer build de un clon limpio | unos 3,5 minutos |
| `clean assembleDebug` con daemon nuevo | unos 106 segundos |
| `assembleDebug` con daemon caliente | unos 18 segundos |

## Instalar y ejecutar en un dispositivo

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.example.vocabulario/.MainActivity
```

## Controles locales (hooks de Git)

El repositorio incluye hooks en `.githooks/`. Git no los activa solo:
en cada clon nuevo hay que ejecutar una vez

```powershell
git config core.hooksPath .githooks
```

- `pre-commit`: revisa espacios sobrantes y marcas de conflicto en lo
  preparado (segundos).
- `pre-push`: ejecuta `./gradlew check` (pruebas y lint; de unos
  segundos a unos 2 minutos).

Se pueden saltar con `--no-verify`: son un aviso rápido, no una
garantía. Ver ADR 0007.

## Preparación opcional del entorno

Gradle guarda su caché en `C:\Users\<usuario>\.gradle`. Si el disco C:
tiene poco espacio, se puede mover definiendo la variable de usuario
`GRADLE_USER_HOME` con otra ruta (en este proyecto, `D:\gradle-home`).
Es una configuración de la máquina, no del repositorio. Ver ADR 0003.

## Equipos con poca memoria

`gradle.properties` limita la memoria de Gradle (`-Xmx768m`,
`MaxMetaspaceSize=512m`). Son valores empíricos, no una recomendación.
Con 256 MB de Metaspace el daemon se quedó sin memoria. Ver
`docs/deudas.md`.

## Documentación

- `docs/adr/`: decisiones de diseño y de entorno, una por archivo.
- `docs/deudas.md`: pendientes abiertos.
