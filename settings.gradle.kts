pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        // Agregamos el repositorio de JetBrains Space donde se publica el plugin de Compose
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }

    plugins {

        id("com.android.application") version "8.9.1" apply false
        id("com.android.library")     version "8.9.1" apply false
        id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "APP NETS"
include(":app")
include(":curveNavX")
