package br.pucpr.authserver.errors

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
