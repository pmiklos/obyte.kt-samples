import app.obyte.client.compose.Wallet
import app.obyte.client.protocol.UnitHash
import react.dom.render
import react.router.dom.routeLink
import kotlin.browser.document

val BASE_ASSET = UnitHash("base")
val GIFT_ASSET = UnitHash("DoKluDWEGRYdILk6gYZEreei0qpq01daYhr5cLqyl4o=")

fun main() {
    render(document.getElementById("root")) {
        child(App::class) {
            val wallet = Wallet.fromSeed("ThiS SH0uLd L1Ve iN THe B4ck3nd, bUt it's JuST a D3m0")
            console.log("GiftCard Creator Address: ${wallet.address.value}")

            attrs {
                this.wallet = wallet
            }
        }
    }
}
