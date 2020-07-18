import app.obyte.client.*
import app.obyte.client.protocol.JustSaying
import app.obyte.client.protocol.Request
import app.obyte.client.protocol.Response
import app.obyte.client.protocol.UnitHash
import kotlinx.html.dom.append
import kotlinx.html.js.p
import kotlinx.html.js.textArea
import kotlin.browser.document
import kotlin.browser.window
import kotlin.coroutines.*

suspend fun main() {

    ObyteClient().connect(ObyteTestHub) {

        onConnected {
            document.body!!.append.p { +"Connected" }
        }

        on<JustSaying.Version> {
            document.body!!.append.p { +"RECEIVED: $it" }
        }

        on<Request.Subscribe> { request ->
            val response = Response.Subscribed(request.tag)
            document.body!!.append.p { +"RECEIVED: $request" }
            document.body!!.append.p { +"SENDING: $response" }
            respond(response)

            launch {
                while (true) {
                    delay(10000)
                    heartbeat()
                }
            }

            getWitnesses()?.let {
                document.body!!.append.p { +"RECEIVED: $it" }
            }

            getJoint(UnitHash("3/9rpEBQWTsxtUxlq+JiZUO8UM36A9kZMndhCaGrFnw="))?.let {
                document.body!!.append.p { +"RECEIVED $it" }
            }
        }

        on<JustSaying.ExchangeRates> { rates ->
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