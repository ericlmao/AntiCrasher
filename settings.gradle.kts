rootProject.name = "AntiCrasher"
include(":common", ":api", ":bukkit", ":velocity")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
