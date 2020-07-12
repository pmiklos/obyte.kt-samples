val coroutines_version: String by project

plugins {
    kotlin("multiplatform") version "1.3.72"
}

group = "app.obyte.client.samples"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvm()
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("app.obyte.client:obytekt:0.0.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("app.obyte.client:obytekt-jvm:0.0.1")
                runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
            }
        }
    }
}