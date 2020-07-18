val obytektVersion: String by project

plugins {
    id("org.jetbrains.kotlin.js")
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("app.obyte.client:obytekt-js:$obytektVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")

    // declare NPM dependencies to fix bugs with ktor client build
    implementation(npm("text-encoding"))
    implementation(npm("bufferutil"))
    implementation(npm("utf-8-validate"))
    implementation(npm("abort-controller"))
    implementation(npm("fs"))

}

kotlin.target.browser {
}