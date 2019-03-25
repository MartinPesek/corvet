package net.orbu.corvet.web.controller

import org.apache.logging.log4j.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import javax.servlet.http.*

@Controller
class UploadController {

    val log = LogManager.getLogger(this.javaClass.name)!!

    @GetMapping("/")
    fun index(): String {
        return "index.html"
    }

    @RequestMapping(value = ["/"], method = [RequestMethod.POST])
    @ResponseBody
    fun saveImage(@ModelAttribute(value = "data") data: String, request: HttpServletRequest, response: HttpServletResponse): String {
        if (data.isBlank()) {
            log.error("Data is empty, nothing to save.")
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return "No data received."
        }

        val resultUrl: String? = if (data.startsWith("http")) {
            null
        } else {
            saveBase64ToFile(data)
        }

        if (resultUrl == null) {
            log.error("No file saved. Data: $data")
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return "Unable to parse data."
        }

        return resultUrl
    }

    private fun saveBase64ToFile(data: String): String? {
        val dataSequence = data.splitToSequence(":", ";", ",")

        var fileType: String? = null
        var base64Data: String? = null

        val iterator = dataSequence.iterator()
        while (iterator.hasNext()) {
            when (iterator.next().toLowerCase()) {
                "data" -> fileType = if (iterator.hasNext()) iterator.next() else null
                "base64" -> base64Data = if (iterator.hasNext()) iterator.next() else null
            }
        }

        TODO("handle result")

        return null
    }
}
