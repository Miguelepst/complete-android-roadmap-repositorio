plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.vocabulario"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.vocabulario"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    lint {
        warningsAsErrors = true
        baseline = file("lint-baseline.xml")
        // Avisos de "hay una versión más nueva": harían fallar el build por
        // el paso del tiempo y no por un cambio nuestro. Las versiones se
        // fijan a propósito (ADR 0002).
        ignore += "AndroidGradlePluginVersion"
        ignore += "GradleDependency"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
