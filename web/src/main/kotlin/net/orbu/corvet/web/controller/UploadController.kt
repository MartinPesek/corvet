package net.orbu.corvet.web.controller

import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class UploadController {

    val log = LogManager.getLogger(this.javaClass.name)!!

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @RequestMapping(value = ["/"], method = [RequestMethod.POST])
    @ResponseBody
    fun saveImage(@ModelAttribute(value = "data") data: String, request: HttpServletRequest, response: HttpServletResponse): String {
        if (data.isBlank()) {
            log.error("Data is empty, nothing to save.")
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return "No data received."
        }

        log.info("Received: ${data.substring(0 until data.indexOf(","))}, total size: ${data.length}")

        return "url"
    }
}
