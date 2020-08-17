import app.obyte.client.compose.Wallet
import app.obyte.client.protocol.Address
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import react.*
import react.dom.*

external interface GiftCardProps: RProps {
    var token: String?
    var address: Address?
    var stableBalance: Long?
    var pendingBalance: Long?
    var onGiftCardOpened: (Wallet) -> Unit
    var onPay: (Address) -> Unit
}

external interface GiftCardState: RState {
    var pin: String?
    var name: String?
}

class GiftCardComponent(props: GiftCardProps): RComponent<GiftCardProps, GiftCardState>(props) {

    override fun RBuilder.render() {
        if (state.pin == null) return askForPin()
        if (state.name == null) return askForPin()
        if (props.address == null) return invalidPin()

        showGiftCard()
    }

    private fun RBuilder.showGiftCard() {
        div(classes = "giftcard text-center mt-5 pt-2 pb-5 px-5 rounded") {
            div(classes = "giftcard-brand text-left p-1") {
                +GIFT_ASSET.value.substring(0, 5)
            }
            h3 { +"${state.name}'s Gift Card" }
            h1 {
                +"${props.stableBalance}"
                props.pendingBalance?.let { pending ->
                    if (pending > 0) {
                        span(classes = "small text-muted") { +" ($pending)" }
                    }
                }
            }
            p(classes = "giftcard-number") {
                props.address?.let {
                    +it.value
                }
            }
            div {
                val hasBalance = props.stableBalance?.let { it > 0L } ?: false

                form(classes = "form") {
                    div(classes = "input-group") {
                        input(classes = "form-control", name = "recipient", type = InputType.text) {
                            attrs {
                                readonly = !hasBalance
                                placeholder = "pay to address"
                                required = true
                                pattern = "[A-Z2-7]{32}"
                                size = "35"
                            }
                        }

                        div(classes = "input-group-append") {
                            button(classes = "btn btn-outline-primary") {
                                attrs {
                                    disabled = !hasBalance
                                    type = ButtonType.submit
                                }
                                +"Pay"
                            }
                        }
                    }
                    attrs {
                        onSubmitFunction = {
                            it.preventDefault()

                            val form = it.target as HTMLFormElement
                            val recipient = form[0] as HTMLInputElement

                            props.onPay(Address(recipient.value))
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.askForPin() {
        div(classes = "text-center bg-light mt-5 p-5 rounded") {
            h1 {
                +"You got a gift card!"
            }
            form(classes = "form") {
                div(classes = "form-group text-left") {
                    label { +"Your Name"}
                    input(InputType.text, classes = "form-control", name = "name") {
                        attrs {
                            size = "30"
                        }
                    }
                    small(classes = "text-muted") { +"Your name as displayed on the card"}
                }

                div(classes = "form-group text-left") {
                    label { +"PIN" }
                    input(InputType.password, classes = "form-control", name = "pin") {
                        attrs {
                            size = "10"
                        }
                    }
                    small(classes = "text-muted") { +"The PIN sent to you" }
                }

                button(classes = "btn btn-primary mt-3") {
                    attrs {
                        type = ButtonType.submit
                    }
                    +"Open Gift Card"
                }

                attrs {
                    onSubmitFunction = {
                        it.preventDefault()

                        val form = it.target as HTMLFormElement
                        val name = form[0] as HTMLInputElement
                        val pin = form[1] as HTMLInputElement
                        val token = props.token ?: ""

                        createWallet(token, name.value, pin.value)?.let { giftCard ->
                            props.onGiftCardOpened(giftCard)
                        }

                        setState {
                            this.pin = pin.value
                            this.name = name.value
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.invalidPin() {
        div(classes = "text-center bg-light mt-5 p-5 rounded") {
            h1 { +"Invalid PIN" }
            button(classes = "btn btn-primary mt-3") {
                attrs {
                    type = ButtonType.button
                    onClickFunction = {
                        setState {
                            this.pin = null
                        }
                    }
                }
                +"Retry"
            }
        }
    }
}

fun RBuilder.giftCard(handler: GiftCardProps.() -> Unit): ReactElement {
    return child(GiftCardComponent::class) {
        this.attrs(handler)
    }
}
