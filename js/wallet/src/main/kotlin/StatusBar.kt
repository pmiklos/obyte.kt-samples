import react.*
import react.dom.a
import react.dom.div
import react.dom.span

external interface StatusBarProps : RProps {
    var message: Any?
}

class StatusBar : RComponent<StatusBarProps, RState>() {
    override fun RBuilder.render() {
        val message = props.message
        if (message != null) {
            div(classes = "bg-info p-3") {
                when (message) {
                    is String -> span { +message }
                    is PaymentAccepted -> a(
                        href = "https://testnetexplorer.obyte.org/#${message.unit.value}",
                        target = "_blank",
                        classes = "text-light"
                    ) {
                        +"Payment to ${message.recipient.value}"
                    }
                }
            }
        }
    }
}

fun RBuilder.statusBar(handler: StatusBarProps.() -> Unit): ReactElement {
    return child(StatusBar::class) {
        this.attrs(handler)
    }
}
