import app.obyte.client.compose.Wallet
import app.obyte.client.newAddressToWatch
import app.obyte.client.on
import app.obyte.client.postJoint
import app.obyte.client.protocol.*
import kotlinx.coroutines.cancel
import react.*

external interface AppState : RState {
    var wallet: Wallet?
    var balance: Map<UnitHash, Balance>?
    var obyte: Obyte?
    var statusMessage: Any?
}


data class PaymentAccepted(
    val recipient: Address,
    val amount: Long,
    val asset: UnitHash?,
    val unit: UnitHash
)

class App : RComponent<RProps, AppState>() {

    override fun RBuilder.render() {
        val currentWallet = state.wallet

        if (currentWallet == null) {
            createWallet {
                onWalletCreated = { wallet ->
                    val obyte = Obyte {
                        onConnected {
                            newAddressToWatch(wallet.address)
                            setState {
                                this.statusMessage = "Connected"
                            }
                        }

                        on<JustSaying.HaveUpdates> {
                            getBalance(wallet.address) { balance ->
                                setState {
                                    this.balance = balance
                                }
                            }
                        }
                        on<JustSaying.Joint> {
                            getBalance(wallet.address) { balance ->
                                setState {
                                    this.balance = balance
                                }
                            }
                        }
                    }

                    obyte.start()

                    setState {
                        this.wallet = wallet
                        this.obyte = obyte
                        this.statusMessage = "Connecting..."
                    }
                }
            }
        } else {
            openWallet {
                wallet = currentWallet
                balance = state.balance
                onPayment = { recipient, amount, asset ->
                    state.obyte?.run {
                        val unit = composer.unit(wallet) {
                            payment(recipient, amount, asset)
                        }
                        val result = postJoint(unit)
                        when (result?.response) {
                            "accepted" -> setState {
                                statusMessage = PaymentAccepted(
                                    recipient = recipient,
                                    amount = amount,
                                    asset = asset,
                                    unit = unit.unit
                                )
                            }
                            null -> setState {
                                statusMessage = "Something went wrong"
                            }
                            else -> setState {
                                statusMessage = "Payment failed: ${result.response}"
                            }
                        }

                        getBalance(wallet.address) { balance ->
                            setState {
                                this.balance = balance
                            }
                        }
                    }
                }
                onClose = {
                    state.obyte?.cancel("Stop")
                    setState {
                        wallet = null
                        balance = null
                        statusMessage = null
                    }
                }
            }
            statusBar {
                message = state.statusMessage
            }
        }
    }
}


