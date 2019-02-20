package net.orbu.corvet.web.controller

import net.orbu.corvet.web.dto.SaveImageResponse
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class UploadController {

    val log = LogManager.getLogger(this.javaClass.name)!!

    @GetMapping("/")
    fun index(): String {
        return "index.html"
    }

    @RequestMapping(value = ["/"], method = [RequestMethod.POST])
    @ResponseBody
    fun saveImage(@ModelAttribute(value = "data") data: String, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<SaveImageResponse> {
        if (data.isBlank()) {
//            log.error("Data is empty, nothing to save.")
            throw RuntimeException("No data received.")
//            response.status = HttpServletResponse.SC_BAD_REQUEST
//            return "No data received."
        }

        if (data == "1") {
            throw RuntimeException("Incorrect format of data.")
        }

//        log.info("Received: ${data.substring(0 until data.indexOf(","))}, total size: ${data.length}")

        return ResponseEntity(SaveImageResponse("https://orbu.net"), HttpStatus.OK)
    }
}
