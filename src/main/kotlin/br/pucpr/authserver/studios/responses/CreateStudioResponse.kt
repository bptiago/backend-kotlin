package br.pucpr.authserver.studios.responses

import br.pucpr.authserver.studios.Studio
import java.time.LocalDate

data class CreateStudioResponse (
    val id: Long,
    val name: String,
    val location: String,
    val creationDate: LocalDate
) {
    constructor(studio: Studio): this(
        id = studio.id!!,
        name = studio.name,
        location = studio.location,
        creationDate = studio.creationDate
    )
}