import js.externals.jquery.JQuery.Deferred
import js.externals.jquery.JQuery.jqXHR
import js.externals.jquery.JQueryAjaxSettings
import js.externals.jquery.JQueryPromiseCallback
import js.externals.jquery.JQueryXHR
import js.externals.jquery.jQuery
import org.w3c.dom.DataTransferItem
import org.w3c.dom.DataTransferItemList
import org.w3c.dom.HTMLElement
import org.w3c.dom.clipboard.ClipboardEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.get
import org.w3c.xhr.FormData
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    val rte = document.getElementById("rte") as? HTMLElement ?: error("Unable to locate rte element.")

    rte.focus()
    rte.addEventListener("paste", EventListener { event: Event -> window.setTimeout(onPaste(event), 1) })
}

fun onPaste(event: Event) {
    if (event is ClipboardEvent) {
//        event.stopPropagation()
//        event.preventDefault()

        val data = event.clipboardData ?: return

        if (data.items.length == 0) {
            console.info("Nothing was pasted.")
            return
        }

        for (item in data.items.wrap()) {
            console.info("type: ${item.type}, kind: ${item.kind}")
            item.getAsString { console.info("string: $it") }
        }

        for (i in 0 until data.items.length) {
            val item = data.items[i] ?: continue
            console.info("type: ${item.type}, kind: ${item.kind}")
            item.getAsString { console.info("string: $it") }
        }

        val item = data.items[0] ?: error("items[0] is null.")
        when (item.type.toLowerCase()) {
            "image/png", "image/jpeg", "image/gif" -> {
                console.info("Supported image type pasted: ${item.type}")
                val file = item.getAsFile() ?: error("Unable to get a file from item.")
                console.log("Retrieved file from item, name: ${file.name}")
            }

            "text/html" -> console.info("Text/html code pasted.")
            else -> error("Unsupported type pasted: ${item.type}")
        }

        println("No. of items pasted: ${data.items.length}")

        val rteImg = jQuery("#rte").find("img")
        if (rteImg.length == 1) {
            val formData = FormData()
            formData.append("data", rteImg.attr("src")!!)

//            val settings: JQueryAjaxSettings = object : JQueryAjaxSettings {}
//            settings.processData = false

//            val aa = jQuery.ajaxSetup(object : JQueryAjaxSettings {
//                override val success: ((data: Any, textStatus: String, jqXHR: JQueryXHR) -> Any)?
//                    get() = { result, textStatus, jqXHR ->
//                        {
//                            if (result is String) {
//
//                            }
//                        }
//                    }
//            })
            jQuery.ajax("", object : JQueryAjaxSettings {}.apply {
                url = "/"
                type = "POST"
                this.data = formData
                processData = false
                contentType = false
            })


        } else {
            error("No img object was pasted!")
        }
    }
}

fun onSuccess(result: String) {

}

fun DataTransferItemList.wrap(): Iterator<DataTransferItem> {
    return object : Iterator<DataTransferItem> {
        private var index = 0
        override fun hasNext(): Boolean = index < this@wrap.length
        override fun next(): DataTransferItem = this@wrap[index++] ?: error("Item is null.")
    }
}

