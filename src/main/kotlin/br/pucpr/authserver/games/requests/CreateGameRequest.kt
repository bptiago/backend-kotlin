package br.pucpr.authserver.games.requests

import br.pucpr.authserver.games.Game
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CreateGameRequest (
    @field:NotNull
    val fkStudioId: Long,
    @field:NotBlank
    val name: String?,
    @field:NotBlank
    val overview: String?,
    @field:NotNull
    val launchDate: LocalDate?,
    @field:NotBlank
    val studio: String?

) {
    fun toGame(): Game = Game(
        id = null,
        name = name!!,
        overview = overview!!,
        launchDate = launchDate ?: LocalDate.now(),
        studio = studio!!
    )
}