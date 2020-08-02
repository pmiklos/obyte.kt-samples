val obytektVersion: String by project

plugins {
    id("org.jetbrains.kotlin.js")
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("app.obyte.client:obytekt-js:$obytektVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")

    implementation(npm("create-hash"))
    implementation(npm("thirty-two"))
    implementation(npm("secp256k1"))

    // declare NPM dependencies to fix bugs with ktor client build
    implementation(npm("text-encoding"))
    implementation(npm("bufferutil"))
    implementation(npm("utf-8-validate"))
    implementation(npm("abort-controller"))
    implementation(npm("fs"))

}

kotlin.target.browser {
    dceTask {
        keep("ktor-ktor-io.\$\$importsForInline\$\$.ktor-ktor-io.io.ktor.utils.io")
    }
}