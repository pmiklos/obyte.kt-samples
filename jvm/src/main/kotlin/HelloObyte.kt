import app.obyte.client.*
import app.obyte.client.protocol.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main() = runBlocking {
    ObyteClient().connect(ObyteTestHub) {

        on<Message.Request.Subscribe> { request ->
            subscribe(request.tag)

            launch {
                while (true) {
                    delay(15000)
                    heartbeat()
                }
            }
        }

        on<Message.JustSaying.UpgradeRequired> {
            println("Exiting")
            exitProcess(-1)
        }

    }
}
