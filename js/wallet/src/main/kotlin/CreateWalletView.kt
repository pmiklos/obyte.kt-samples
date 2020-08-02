import app.obyte.client.compose.Wallet
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import react.*
import react.dom.*

external interface CreateWalletProps : RProps {
    var onWalletCreated: (Wallet) -> Unit
}

class CreateWalletView : RComponent<CreateWalletProps, RState>() {
    override fun RBuilder.render() {
        div(classes = "text-center bg-light mt-5 p-5 rounded") {
            h1(classes = "my-3") {
                +"Open your wallet"
            }
            form(classes = "form") {
                input(classes = "form-control", name = "seed", type = InputType.text) {
                    attrs {
                        placeholder = "a passphrase to your wallet"
                        size = "50"
                    }
                }
                button(classes = "btn btn-primary mt-3") {
                    attrs {
                        type = ButtonType.submit
                    }
                    +"Open wallet"
                }
                attrs {
                    onSubmitFunction = {
                        it.preventDefault()

                        val form = it.target as HTMLFormElement
                        val seed = form[0] as HTMLInputElement

                        try {
                            val wallet = Wallet.fromSeed(seed.value)
                            props.onWalletCreated(wallet)
                        } catch (e: Exception) {
                            console.error(e.message)
                        }
                    }
                }
            }
            p(classes = "mt-3 w500px") {
                +"""
                    NOTE: this pass phrase is not the commonly known seed words. You can type in any text here,
                    that will be used to derive the private key from. If you want to recover your wallet later, try to
                    remember what you type in here.
                """.trimIndent()
            }
        }
    }
}

fun RBuilder.createWallet(handler: CreateWalletProps.() -> Unit): ReactElement {
    return child(CreateWalletView::class) {
        this.attrs(handler)
    }
}
