import app.obyte.client.compose.Wallet
import app.obyte.client.protocol.Address
import kotlinx.html.ATarget
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import react.*
import react.dom.*
import react.router.dom.routeLink
import kotlin.random.Random

data class GiftCardCreation(
    val address: Address,
    val token: String,
    val pin: String,
    val name: String
)

external interface IndexProps : RProps {
    var onGiftCardCreated: (event: GiftCardCreation) -> Unit
}

external interface IndexState : RState {
    var giftCard: GiftCardCreation?
    var giftCardLoaded: Boolean?
}

class IndexComponent(props: IndexProps) : RComponent<IndexProps, IndexState>(props) {

    override fun IndexState.init(props: IndexProps) {
        giftCardLoaded = false
    }

    override fun RBuilder.render() {
        if (state.giftCard == null) return createCard()
        reviewCard()
    }

    private fun RBuilder.createCard() {
        div(classes = "text-center bg-light mt-5 p-5 rounded") {
            h1(classes = "my-3") {
                +"Giftcard Creator"
            }

            form(classes = "form") {
                input(classes = "form-control font-larger", name = "name", type = InputType.text) {
                    attrs {
                        placeholder = "name to display on card"
                        maxLength = "30"
                        size = "30"
                        required = true
                    }
                }
                button(classes = "btn btn-primary mt-3") {
                    attrs {
                        type = ButtonType.submit
                    }
                    +"Create Giftcard"
                }

                attrs {
                    onSubmitFunction = {
                        it.preventDefault()

                        val form = it.target as HTMLFormElement
                        val name = form[0] as HTMLInputElement
                        val pin = Random.nextInt(100_000, 1_000_000).toString()

                        var token: String
                        var wallet: Wallet?

                        do {
                            token = Buffer.from(Random.nextBytes(32)).toString("hex")
                            wallet = createWallet(token, name.value, pin)
                        } while (wallet == null)

                        setState {
                            giftCard = GiftCardCreation(
                                address = wallet.address,
                                token = token,
                                pin = pin,
                                name = name.value
                            )
                            giftCardLoaded = false
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.reviewCard() {
        div(classes = "text-center bg-light mt-5 p-5 rounded") {
            if (state.giftCardLoaded == true) {
                p(classes = "alert alert-success") {
                    +"Remember to share the PIN code and the gift card URL with ${state.giftCard?.name}!"
                }
            }
            h1 {
                +"Gift card details"
            }
            val giftCard = state.giftCard

            if (giftCard != null) {
                p {
                    +giftCard.address.value
                }
                p {
                    +"For: ${giftCard.name}"
                    br { }
                    +"PIN: ${giftCard.pin}"
                }
                p {
                    routeLink("/giftcard/${giftCard.token}") {
                        +"Link to GiftCard (share with ${giftCard.name})"
                        attrs.asDynamic().target = ATarget.blank
                    }
                }

                button(classes = "btn btn-primary") {
                    if (state.giftCardLoaded == true) {
                        +"Loaded!"
                    } else {
                        +"Yes, top it up!"
                    }

                    attrs {
                        disabled = state.giftCardLoaded == true

                        onClickFunction = {
                            props.onGiftCardCreated(giftCard)
                            setState {
                                giftCardLoaded = true
                            }
                        }
                    }
                }
            } else {
                p { +"No gift card created" }
            }

            button(classes = "btn btn-outline-primary mx-3") {
                +"Create another one"

                attrs {
                    onClickFunction = {
                        setState {
                            this.giftCard = null
                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.index(handler: IndexProps.() -> Unit): ReactElement {
    return child(IndexComponent::class) {
        this.attrs(handler)
    }
}
