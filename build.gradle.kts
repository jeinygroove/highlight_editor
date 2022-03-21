import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.5.30"
    id("org.jetbrains.compose") version "1.0.0-alpha4-build331"
    kotlin("plugin.serialization") version "1.4.21"
}

val ktorVersion = "1.6.7"
kotlin {
    jvm("desktop") {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
            }
        }
        named("desktopMain") {
            dependencies {
                implementation(compose.desktop.macos_arm64) // or .common for other os
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
                implementation("ai.grazie.client:client-okhttp-jvm:0.2.12")
                implementation("ai.grazie.gec:gec-agg-cloud-engine-jvm:0.2.12")
                implementation("ai.grazie.nlp:nlp-langs:0.2.12")
            }
        }
    }
}

version = "0.1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
    maven("https://packages.jetbrains.team/maven/p/skija/maven")
    google()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    //kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xopt-in=kotlin.Experimental", "-Xallow-jvm-ir-dependencies")
}