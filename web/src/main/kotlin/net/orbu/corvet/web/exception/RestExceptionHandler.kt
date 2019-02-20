package net.orbu.corvet.web.exception

import net.orbu.corvet.web.dto.Failure
import net.orbu.corvet.web.dto.Failures
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
@Component
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    val log = LogManager.getLogger(this.javaClass.name)!!

    @ExceptionHandler
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<Failures> {
        log.error("Runtime error caught", ex)
        return ResponseEntity(Failures(Failure(ex.message ?: "Unknown error.")), HttpStatus.BAD_REQUEST)
    }

}
