package br.pucpr.authserver.studios.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class UpdateStudioRequest(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val location: String,

    @field:NotNull
    val creationDate: LocalDate,
)