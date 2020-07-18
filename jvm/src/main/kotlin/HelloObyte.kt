import app.obyte.client.*
import app.obyte.client.protocol.JustSaying
import app.obyte.client.protocol.Message
import app.obyte.client.protocol.Request
import app.obyte.client.protocol.UnitHash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main() = runBlocking {
    ObyteClient().connect(ObyteTestHub) {

        on<Request.Subscribe> { request ->
            subscribe(request.tag)

            launch {
                while (true) {
                    delay(15000)
                    heartbeat()
                }
            }

            getWitnesses()?.apply {
                getGetParentsAndLastBallAndWitnessesUnit(witnesses)
            }

            getDefinitionForAddress("2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX")

            getJoint(UnitHash("3/9rpEBQWTsxtUxlq+JiZUO8UM36A9kZMndhCaGrFnw="))?.apply {
                println(joint.unit.unit)
            }
        }

        on<JustSaying.UpgradeRequired> {
            println("Exiting")
            exitProcess(-1)
        }

    }
}
