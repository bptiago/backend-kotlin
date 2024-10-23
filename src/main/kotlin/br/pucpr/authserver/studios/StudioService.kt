package br.pucpr.authserver.studios

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.games.GameRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class StudioService (
    val repository: StudioRepository,
) {
    fun insert(studio: Studio): Studio {
        if (studio.id != null) {
            throw IllegalArgumentException("Registro com ID ${studio.id} já inserido")
        }

        return repository.save(studio)
    }

    fun getStudio(id: Long): Studio = repository.findByIdOrNull(id) ?: throw NotFoundException("Não foi encontrado studio com ID $id")

    fun update(id: Long, updatedStudio: Studio): Studio {
        val existingStudio = getStudio(id)
        // Update fields of the existing studio with the new data
        existingStudio.name = updatedStudio.name
        existingStudio.location = updatedStudio.location
        existingStudio.creationDate = updatedStudio.creationDate
        existingStudio.games = updatedStudio.games
        return repository.save(existingStudio)
    }

    fun delete(id: Long) {
        val studio = getStudio(id)
        repository.delete(studio)
    }
}