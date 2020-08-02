import app.obyte.client.*
import app.obyte.client.protocol.JustSaying
import app.obyte.client.protocol.Request
import app.obyte.client.protocol.Response
import app.obyte.client.protocol.UnitHash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.html.dom.append
import kotlinx.html.p
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document
import kotlin.coroutines.CoroutineContext

fun main() {
    document.addEventListener("DOMContentLoaded", {
        Application().start()
    })
}

class Application(override val coroutineContext: CoroutineContext = Job()) : CoroutineScope {

    fun start() = launch {

        val console = document.getElementById("console")!! as HTMLDivElement

        ObyteClient().connect(ObyteTestHub) {

            onConnected {
                console.append.p { +"Connected" }
            }

            on<JustSaying.Version> {
                console.append.p { +"RECEIVED: $it" }
            }

            on<Request.Subscribe> { request ->
                val response = Response.Subscribed(request.tag)
                console.append.p { +"RECEIVED: $request" }
                console.append.p { +"SENDING: $response" }
                respond(response)

                launch {
                    while (true) {
                        delay(10000)
                        heartbeat()
                    }
                }

                getWitnesses()?.let {
                    console.append.p { +"RECEIVED: $it" }
                }

                getJoint(UnitHash("3/9rpEBQWTsxtUxlq+JiZUO8UM36A9kZMndhCaGrFnw="))?.let {
                    console.append.p { +"RECEIVED $it" }
                }
            }

            on<JustSaying.ExchangeRates> { rates ->
                console.append.p { +"RECEIVED: $rates" }
            }

        }
    }
}