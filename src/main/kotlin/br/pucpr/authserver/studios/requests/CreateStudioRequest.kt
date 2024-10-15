package br.pucpr.authserver.games.requests

import br.pucpr.authserver.games.Game
import br.pucpr.authserver.studios.Studio
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CreateStudioRequest (
    @field:NotBlank
    val name: String?,
    @field:NotBlank
    val location: String?,
    @field:NotNull
    val creationDate: LocalDate?,
) {
    fun toStudio(): Studio = Studio(
        id = null,
        name = name!!,
        location = location!!,
        creationDate = creationDate!!,
        games = mutableListOf()
    )
}