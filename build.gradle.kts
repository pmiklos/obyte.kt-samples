plugins {
    kotlin("jvm") version "1.3.72"
}

allprojects {

    group = "app.obyte.client.samples"
    version = "0.0.1"

    repositories {
        jcenter()
        mavenCentral()
        mavenLocal()
        maven {
            name = "ObyteKt"
            url = uri("https://maven.pkg.github.com/pmiklos/obytekt")
            credentials {
                username = project.findProperty("gpr.user")?.toString() ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key")?.toString() ?: System.getenv("TOKEN")
            }
        }
    }

}

tasks.register("gh-pages", Copy::class) {
    dependsOn("assemble")

    mkdir("$buildDir/gh-pages")
    destinationDir = file("$buildDir/gh-pages")

    into("simple") {
        from(project("js:simple").file("build/distributions"))
    }
    into("wallet") {
        from(project("js:wallet").file("build/distributions"))
    }
}
