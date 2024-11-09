package br.pucpr.authserver.games

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.games.requests.UpdateGameRequest

import br.pucpr.authserver.studios.StudioRepository
import br.pucpr.authserver.utils.SortDir
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GameService (
    val repository: GameRepository,
    val studioRepository: StudioRepository
) {
    fun insert(game: Game, studioId: Long): Game {
        if (game.id != null) {
            throw IllegalArgumentException("Registro com ID ${game.id} já inserido!")
        }
        // Não tá aparecendo mensagem de exception
        val studio = studioRepository.findByIdOrNull(studioId) ?: throw BadRequestException("Não há studio cadastrado com ID $studioId")
        studio.games.add(game)
        studioRepository.save(studio)
        log.info("Insert game (name={}) for studio with ID={}", game.name, studioId)
        return studio.games.last()
    }

    fun list(sortDir: SortDir): List<Game> {
        return when (sortDir) {
            SortDir.ASC -> repository.findAll()
            SortDir.DESC -> repository.findAll(Sort.by("id").reverse())
        }
    }

    fun getStudio(id: Long): Game =
        repository.findByIdOrNull(id) ?: throw NotFoundException("Não foi encontrado game com ID $id")

    fun update(id: Long, gameRequest: UpdateGameRequest): Game {
        val existingGame = getStudio(id)
        log.info("Update information from game with ID={}. Update: name={}, overview={}, launchDate={}", existingGame.id, gameRequest.name, gameRequest.overview, gameRequest.launchDate)
        existingGame.name = gameRequest.name
        existingGame.overview = gameRequest.overview
        existingGame.launchDate = gameRequest.launchDate
        return repository.save(existingGame)
    }

    fun delete(id: Long) {
        val game = getStudio(id)
        log.info("Delete game with ID={}", game.id)
        return repository.delete(game)
    }

    companion object {
        private val log = LoggerFactory.getLogger(GameService::class.java)
    }
}