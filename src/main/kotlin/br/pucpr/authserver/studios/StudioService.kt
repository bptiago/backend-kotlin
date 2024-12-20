package br.pucpr.authserver.studios

import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.studios.requests.UpdateStudioRequest
import br.pucpr.authserver.utils.SortDir
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
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
        log.info("Insert studio with name={}, location={}", studio.name, studio.location)
        return repository.save(studio)
    }

    fun getStudio(id: Long): Studio = repository.findByIdOrNull(id) ?: throw NotFoundException("Não foi encontrado studio com ID $id")

    fun list(sortDir: SortDir): List<Studio> {
        return when (sortDir) {
            SortDir.ASC -> repository.findAll()
            SortDir.DESC -> repository.findAll(Sort.by("id").reverse())
        }
    }

    fun update(id: Long, studioRequest: UpdateStudioRequest): Studio {
        val existingStudio = getStudio(id)
        log.info("Update information from studio with ID={}. Update: name={}, location={}, creationDate={}", existingStudio.id, studioRequest.name, existingStudio.location, existingStudio.creationDate)
        existingStudio.name = studioRequest.name
        existingStudio.location = studioRequest.location
        existingStudio.creationDate = studioRequest.creationDate
        return repository.save(existingStudio)
    }

    fun delete(id: Long) {
        val studio = getStudio(id)
        log.info("Delete studio with ID={}", id)
        return repository.delete(studio)
    }

    companion object {
        private val log = LoggerFactory.getLogger(StudioService::class.java)
    }
}