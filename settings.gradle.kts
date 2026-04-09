pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            version("agp", "8.2.0")
            version("kotlin", "1.9.20")

            plugin("android-application", "com.android.application").versionRef("agp")
            plugin("kotlin-android", "org.jetbrains.kotlin.android").versionRef("kotlin")
        }
    }
}

rootProject.name = "JuPenTimer"
include(":app")
