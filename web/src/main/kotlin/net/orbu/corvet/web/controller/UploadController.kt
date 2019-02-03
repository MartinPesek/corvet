package net.orbu.corvet.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UploadController {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

}
