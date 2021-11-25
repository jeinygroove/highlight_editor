import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.5.30"
    id("org.jetbrains.compose") version "1.0.0-alpha4-build331"
}

kotlin {
    jvm("desktop")


    sourceSets {
        named("commonMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
            }
        }
        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.linux_x64) // or .common for other os
            }
        }
    }
}

version = "0.5.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
