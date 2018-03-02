package co.orbu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    @Value("${gongyu.version}")
    private String version;

    @GetMapping("/version")
    public String getVersion() {
        return version;
    }

}
