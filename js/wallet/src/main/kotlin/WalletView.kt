import app.obyte.client.compose.Wallet
import app.obyte.client.protocol.Address
import app.obyte.client.protocol.Balance
import app.obyte.client.protocol.UnitHash
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import react.*
import react.dom.*

external interface WalletProps : RProps {
    var wallet: Wallet
    var balance: Map<UnitHash, Balance>?
    var onPayment: (Address, Long, UnitHash?) -> Unit?
    var onClose: () -> Unit?
}

class WalletView : RComponent<WalletProps, RState>() {
    override fun RBuilder.render() {
        div(classes = "bg-secondary text-light mt-5 mb-0") {
            div(classes = "clearfix") {
                button(classes = "btn btn-outline-light float-right m-2") {
                    +"X"
                    attrs {
                        onClickFunction = {
                            props.onClose()
                        }
                    }
                }
            }
            h4(classes = "text-center text-break pt-2 pb-5 px-3 m-0") {
                +props.wallet.address.value
            }
        }

        if (props.balance.isNullOrEmpty()) {
            div(classes = "text-center bg-light py-5 m-0") {
                p {
                    +"No balance yet. Transfer some bytes or assets"
                }
            }
        }

        div(classes = "accordion text-center") {
            attrs {
                id = "accordion-wallet"
            }
            props.balance?.forEach { (asset, balance) ->
                val assetName = if (asset.value == "base") "bytes" else asset.value

                collapsingCard(accordion = "accordion-wallet", id = "${asset.value.hashCode()}") {
                    header {
                        h1(classes = "display-3") { +"${balance.stable}" }
                        if (balance.pending != 0L) {
                            h2(classes = "text-muted") { +"(${balance.pending})" }
                        }
                        p { +assetName }
                    }

                    body {
                        form(classes = "form-inline d-flex justify-content-center") {
                            input(classes = "form-control", type = InputType.text, name = "recipient") {
                                attrs {
                                    placeholder = "recipient address"
                                    required = true
                                    pattern = "[A-Z2-7]{32}"
                                    size = "35"
                                }
                            }
                            input(classes = "form-control m-2", type = InputType.number, name = "amount") {
                                attrs {
                                    placeholder = "amount"
                                    required = true
                                    size = "10"
                                }
                            }
                            button(classes = "btn btn-primary") {
                                attrs {
                                    type = ButtonType.submit
                                }
                                +"Send"
                            }
                            attrs {
                                onSubmitFunction = {
                                    it.preventDefault()

                                    val form = it.target as HTMLFormElement
                                    val recipient = form[0] as HTMLInputElement
                                    val amount = form[1] as HTMLInputElement

                                    if (asset.value == "base") {
                                        props.onPayment(Address(recipient.value), amount.value.toLong(), null)
                                    } else {
                                        props.onPayment(Address(recipient.value), amount.value.toLong(), asset)
                                    }
                                    form.reset()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.openWallet(handler: WalletProps.() -> Unit): ReactElement {
    return child(WalletView::class) {
        this.attrs(handler)
    }
}

fun RBuilder.collapsingCard(
    accordion: String,
    id: String,
    configure: CollapsingCardBody.() -> Unit
) {
    val configuration = CollapsingCardBody()
    configure(configuration)

    div(classes = "card") {
        div(classes = "card-header") {
            attrs {
                this.id = "heading-$id"
            }
            a {
                attrs["data-toggle"] = "collapse"
                attrs["data-target"] = "#collapse-$id"
                attrs["aria-expanded"] = false
                attrs["aria-controls"] = id

                with(configuration) {
                    headerBlock()
                }
            }
        }
        div(classes = "collapse") {
            attrs["id"] = "collapse-$id"
            attrs["data-parent"] = "#$accordion"
            attrs["aria-labelledby"] = "heading-$id"

            div(classes = "card-body") {
                with(configuration) {
                    bodyBlock()
                }
            }
        }
    }
}


class CollapsingCardBody() {
    var headerBlock: RDOMBuilder<A>.() -> Unit = {}
    var bodyBlock: RDOMBuilder<DIV>.() -> Unit = {}

    fun header(block: RDOMBuilder<A>.() -> Unit) {
        headerBlock = block
    }

    fun body(block: RDOMBuilder<DIV>.() -> Unit) {
        bodyBlock = block
    }
}