import app.obyte.client.*
import app.obyte.client.compose.Wallet
import app.obyte.client.protocol.Address
import app.obyte.client.protocol.JustSaying
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

    println("Generating wallet (this may take some time)")

    val wallet = Wallet.fromSeed(phrase)

    println("Oracle address is: ${wallet.address.value}")

    println("Connecting to Obyte...")

    ObyteClient().connect(ObyteTestHub) {

        onConnected {
            println("Connected")
            if (hasEnoughBalance(wallet.address)) {
                postRandomNumber(wallet)
            } else {
                println("Not enough balance yet, waiting for at least 1000 bytes")
                newAddressToWatch(wallet.address)
            }
        }

        on<Request.Subscribe> { request ->
            subscribe(request.tag)

            launch {
                while (true) {
                    delay(15000)
                    heartbeat()
                    print(".")
                }
            }
        }

        on<JustSaying.HaveUpdates> {
            if (hasEnoughBalance(wallet.address)) {
                postRandomNumber(wallet)
            }
        }

        on<JustSaying.Joint> {
            if (hasEnoughBalance(wallet.address)) {
                postRandomNumber(wallet)
            }
        }
    }

}

suspend fun ObyteClientContext.hasEnoughBalance(address: Address): Boolean {
    val balance = getByteBalance(address)
    return balance != null && balance.stable >= 1000
}

suspend fun ObyteClientContext.postRandomNumber(wallet: Wallet) {
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
