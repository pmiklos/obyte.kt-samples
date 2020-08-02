import app.obyte.client.*
import app.obyte.client.protocol.*
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
                getGetParentsAndLastBallAndWitnessesUnit(witnesses)?.apply {
                    request(
                        Request.PickDivisibleCoinsForAmount(
                            addresses = listOf(Address("2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX")),
                            amount = 10000,
                            lastBallMci = lastStableMcBallMci,
                            spendUnconfirmed = SpendUnconfirmed.OWN
                        )
                    )
                }
            }

            getDefinitionForAddress(Address("2FF7PSL7FYXVU5UIQHCVDTTPUOOG75GX"))

            getJoint(UnitHash("YJI2qpj6xALKsS/cBSiMdiKbTEOd4ffIBL5JKcjnXek="))?.apply {
                println(joint.unit.unit)
            }
        }

        on<JustSaying.UpgradeRequired> {
            println("Exiting")
            exitProcess(-1)
        }

    }
}
