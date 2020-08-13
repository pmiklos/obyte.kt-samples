val obytektVersion: String by project

plugins {
    application
    kotlin("jvm")
}

group = "app.obyte.client.samples"
version = "0.0.1"

application {
    mainClassName = "OracleKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("app.obyte.client:obytekt-jvm:$obytektVersion")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    val run by getting(JavaExec::class) {
        standardInput = System.`in`
    }
}
