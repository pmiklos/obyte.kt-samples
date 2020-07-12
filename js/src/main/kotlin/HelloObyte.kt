import app.obyte.client.ObyteClient
import app.obyte.client.ObyteTestHub
import app.obyte.client.connect
import app.obyte.client.on
import app.obyte.client.protocol.Message
import kotlinx.html.dom.append
import kotlinx.html.js.p
import kotlin.browser.document

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
        }

        on<Message.JustSaying.ExchangeRates> { rates ->
            document.body!!.append.p { +"RECEIVED: $rates" }
        }

    }
}
