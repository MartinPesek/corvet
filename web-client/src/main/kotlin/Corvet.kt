import js.externals.jquery.jQuery
import org.w3c.dom.HTMLElement
import org.w3c.dom.clipboard.ClipboardEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.window

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

        window.setTimeout({
            val rteImg = jQuery("#rte").find("img")
            if (rteImg != undefined && rteImg.length == 1) {
                println("found pasted img object")
                val attr = rteImg.attr("src")
                println("attr: $attr")
            } else {
                println("No rte img object was pasted!")
            }
        }, 1)
    }
}
