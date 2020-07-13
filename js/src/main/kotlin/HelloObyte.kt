import app.obyte.client.*
import app.obyte.client.protocol.Message
import kotlinx.html.dom.append
import kotlinx.html.js.p
import kotlin.browser.document
import kotlin.browser.window
import kotlin.coroutines.*

suspend fun main() {

    ObyteClient().connect(ObyteTestHub) {

        onConnected {
            document.body!!.append.p { +"Connected" }
        }

        on<Message.JustSaying.Version> {
            document.body!!.append.p { +"RECEIVED: $it" }
        }

        on<Message.Request.Subscribe> { request ->
            val response = Message.Response.Subscribed(request.tag)
            document.body!!.append.p { +"RECEIVED: $request" }
            document.body!!.append.p { +"SENDING: $response" }
            respond(response)

            launch {
                while (true) {
                    delay(10000)
                    heartbeat()
                }
            }
        }

        on<Message.JustSaying.ExchangeRates> { rates ->
            document.body!!.append.p { +"RECEIVED: $rates" }
        }

    }
}

fun launch(block: suspend () -> Unit) {
    block.startCoroutine(object : Continuation<Unit> {
        override val context = EmptyCoroutineContext
        override fun resumeWith(result: Result<Unit>) = Unit
    })
}

suspend fun delay(ms: Int): Unit = suspendCoroutine { continuation ->
    window.setTimeout({
        continuation.resume(Unit)
    }, ms)
}