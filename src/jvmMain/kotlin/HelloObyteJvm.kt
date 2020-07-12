import app.obyte.client.ObyteClient
import app.obyte.client.ObyteTestHub
import app.obyte.client.connect
import app.obyte.client.on
import app.obyte.client.protocol.Message
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main() = runBlocking {
    ObyteClient {

    }.connect(ObyteTestHub) {

        on<Message.JustSaying.Version> { version ->
            println(version)
            exitProcess(0)
        }

    }
}
