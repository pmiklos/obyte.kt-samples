plugins {
    application
    kotlin("jvm")
}

group = "app.obyte.client.samples"
version = "0.0.1"

application {
    mainClassName = "HelloObyteKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("app.obyte.client:obytekt-jvm:0.0.1")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}