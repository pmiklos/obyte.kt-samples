import app.obyte.client.*
import app.obyte.client.protocol.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class Obyte(
    override val coroutineContext: CoroutineContext = Job(),
    private val configure: ObyteSessionConfiguration.() -> Unit = {}
) : CoroutineScope {

    private var clientContext: ObyteClientContext? = null

    fun start() = launch {
        ObyteClient().connect(ObyteTestHub) {

            on<Request.Subscribe> { request ->
                subscribe(request.tag)
                clientContext = this

                launch {
                    while (true) {
                        delay(15_000)
                        heartbeat()
                    }
                }
            }

            configure(this)
        }
    }

    fun run(block: suspend ObyteClientContext.() -> Unit) {
        launch {
            clientContext?.let { block(it) }
        }
    }
}

suspend fun ObyteClientContext.getBalance(address: Address, block: (Map<UnitHash, Balance>) -> Unit) {
    getBalances(listOf(address))?.let { response ->
        response.balances[address]?.let { balance ->
            block(balance)
        }
    }
}