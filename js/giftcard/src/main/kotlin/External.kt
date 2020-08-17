@JsModule("create-hash")
@JsNonModule
external class CreateHash(algorithm: String) {
    fun update(message: String)
    fun digest(): Uint8Array
}

external class Uint8Array(array: ByteArray) {
    fun toString(encoding: String): String
}

external class Buffer {
    companion object {
        fun from(array: ByteArray): Buffer
    }

    fun toString(encoding: String): String
}
