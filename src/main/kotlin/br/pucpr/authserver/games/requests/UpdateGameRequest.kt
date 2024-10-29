package br.pucpr.authserver.games.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class UpdateGameRequest (
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val overview: String,

    @field:NotNull
    val launchDate: LocalDate
)