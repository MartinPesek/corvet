package net.orbu.corvet.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CorvetApplication

fun main(args: Array<String>) {
    runApplication<CorvetApplication>(*args)
}
