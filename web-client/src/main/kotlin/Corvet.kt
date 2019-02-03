import org.w3c.dom.HTMLElement
import org.w3c.dom.clipboard.ClipboardEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.browser.document

fun main() {
    val rte = document.getElementById("rte") as? HTMLElement ?: return

    rte.focus()
    rte.addEventListener("paste", EventListener { event: Event -> onPaste(event) })
}

fun onPaste(event: Event) {
    if (event is ClipboardEvent) {
//        event.stopPropagation()
//        event.preventDefault()

        val data = event.clipboardData ?: return
        println("No. of items pasted: ${data.items.length}")
    }
}
