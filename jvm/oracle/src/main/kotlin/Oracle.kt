import app.obyte.client.*
import app.obyte.client.compose.Wallet
import app.obyte.client.protocol.Address
import app.obyte.client.protocol.Request
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.SecureRandom
import kotlin.system.exitProcess

val random = SecureRandom()

fun main() = runBlocking {
    println("To open a wallet, enter a random phrase to generate your private key.")
    println("Enter phrase: ")

    val phrase = readLine()

    if (phrase == null) {
        println("No phrase entered, exiting")
        exitProcess(-1)
    }

    println("Connecting to Obyte...")

    ObyteClient().connect(ObyteTestHub) {

        onConnected {
            println("Connected")
            println("Generating wallet (this may take long minutes)")

            val wallet = Wallet.fromSeed(phrase)
            println("Oracle address is: ${wallet.address.value}")

            waitForBalance(wallet.address)

            val unit = composer.unit(wallet) {
                dataFeed {
                    "Obyte.kt" to random.nextInt().toString()
                }
            }

            val result = postJoint(unit)
            if (result?.response == "accepted") {
                println("Successfully posted unit ${unit.unit}")
                exitProcess(0)
            } else {
                println("Failed to post unit: \n$unit")
                exitProcess(-1)
            }
        }

        on<Request.Subscribe> { request ->
            subscribe(request.tag)

            launch {
                while (true) {
                    delay(15000)
                    heartbeat()
                }
            }
        }
    }

}

suspend fun ObyteClientContext.waitForBalance(address: Address) {
    var balance = getByteBalance(address)
    println("Balance: $balance")
    if (balance == null || balance.stable < 1000) {
        println("Not enough balance yet, waiting for at least 1000 bytes")

        while (balance == null || balance.stable < 1000) {
            delay(10000)
            print(".")
            balance = getByteBalance(address)
        }
        println()
    }
}