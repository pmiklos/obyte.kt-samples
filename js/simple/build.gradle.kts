val obytektVersion: String by project

plugins {
    kotlin("js") version "1.4.0"
}

kotlin {
    js {
        browser()
    }
}

dependencies {
    implementation(kotlin("stdlib-js"))
    implementation("app.obyte.client:obytekt-js:$obytektVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.1")

    implementation(npm("create-hash", "^1.2.0"))
    implementation(npm("thirty-two", "^1.0.2"))
    implementation(npm("secp256k1", "^4.0.2"))

    // declare NPM dependencies to fix bugs with ktor client build
    implementation(npm("text-encoding", "^0.7.0"))
    implementation(npm("bufferutil", "^4.0.1"))
    implementation(npm("utf-8-validate", "^5.0.2"))
    implementation(npm("abort-controller", "^3.0.0"))
    implementation(npm("fs", "^0.0.2"))

}
