import app.obyte.client.compose.Wallet
import app.obyte.client.newAddressToWatch
import app.obyte.client.on
import app.obyte.client.postJoint
import app.obyte.client.protocol.Balance
import app.obyte.client.protocol.JustSaying
import react.*
import react.dom.div
import react.dom.p
import react.router.dom.hashRouter
import react.router.dom.route
import react.router.dom.switch


external interface AppProps : RProps {
    var wallet: Wallet?
}

external interface AppState : RState {
    var obyte: Obyte?
    var giftCard: GiftCard?
    var status: String?
}

data class GiftCard(
    var wallet: Wallet? = null,
    var balance: Balance? = null
)

interface GiftCardParams : RProps {
    var token: String
}

class App(props: AppProps) : RComponent<AppProps, AppState>(props) {

    override fun AppState.init(props: AppProps) {
        giftCard = GiftCard()
        obyte = Obyte {
            onConnected {
                props.wallet?.address?.let { address ->
                    getBalance(address) { balances ->
                        val byteBalance = balances[BASE_ASSET]?.total ?: 0L
                        val assetBalance = balances[GIFT_ASSET]?.total ?: 0L
                        console.log("Byte balance: $byteBalance")
                        console.log("Asset balance: $assetBalance")
                    }
                }
                setState {
                    status = "Connected"
                }
            }

            on<JustSaying.HaveUpdates> {
                giftCard?.wallet?.address?.let { address ->
                    getBalance(address) { balances ->
                        setState {
                            state.giftCard?.balance = balances[GIFT_ASSET]
                        }
                    }
                }
            }

            on<JustSaying.Joint> {
                giftCard?.wallet?.address?.let { address ->
                    getBalance(address) { balances ->
                        setState {
                            state.giftCard?.balance = balances[GIFT_ASSET]
                        }
                    }
                }
            }
        }
        obyte?.start()
    }

    override fun RBuilder.render() {
        hashRouter {
            switch {

                route("/", exact = true) {
                    div {
                        index {
                            onGiftCardCreated = { event ->
                                state.obyte?.run {
                                    val wallet = props.wallet ?: return@run

                                    val unit = composer.unit(wallet) {
                                        payment(event.address, 1200)
                                        payment(event.address, 25, GIFT_ASSET)
                                    }

                                    val result = postJoint(unit)

                                    setState {
                                        status = if (result?.response == "accepted") {
                                            "GiftCard loaded: ${unit.unit.value}"
                                        } else {
                                            "Failed to load GiftCard: ${result?.response}"
                                        }
                                    }
                                }
                            }
                        }
                        p(classes = "bg-info text-white") {
                            state.status?.let { +it }
                        }
                    }
                }

                route<GiftCardParams>("/giftcard/:token") { route ->
                    giftCard {
                        token = route.match.params.token
                        stableBalance = state.giftCard?.balance?.stable ?: 0L
                        pendingBalance = state.giftCard?.balance?.pending
                        address = state.giftCard?.wallet?.address

                        onGiftCardOpened = { wallet ->

                            state.obyte?.run {
                                newAddressToWatch(wallet.address)
                            }

                            setState {
                                giftCard?.wallet = wallet
                            }
                        }

                        onPay = { recipient ->
                            state.obyte?.run {
                                val wallet = state.giftCard?.wallet ?: return@run
                                val amount = state.giftCard?.balance?.total ?: return@run

                                if (amount > 0) {
                                    val unit = composer.unit(wallet) {
                                        payment(recipient, amount, GIFT_ASSET)
                                    }

                                    val result = postJoint(unit)
                                    if (result?.response == "accepted") {
                                        console.log("Gift successfully sent in ${unit.unit.value}")

                                        getBalance(wallet.address) { balances ->
                                            setState {
                                                state.giftCard?.balance = balances[GIFT_ASSET]
                                            }
                                        }
                                    } else {
                                        console.error("Failed to spend gift: ${result?.response}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


val Balance.total: Long get() = stable + pending