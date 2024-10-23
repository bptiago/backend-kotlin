package br.pucpr.authserver.games

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.EntityNotFoundException

import br.pucpr.authserver.studios.Studio
import br.pucpr.authserver.studios.StudioRepository
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

        val studio = studioRepository.findByIdOrNull(studioId) ?: throw BadRequestException("Não há studio cadastrado com ID $studioId")
        studio.games.add(game)
        studioRepository.save(studio)
        return studio.games.last()
    }

    fun list(): List<Game> = repository.findAll()

    fun findById(id: Long): Game {
        return repository.findById(id)
            .orElseThrow { EntityNotFoundException("Game not found with id: $id") }
    }

    fun update(id: Long, updatedGame: Game): Game {
        val existingGame = findById(id)
        // Update the fields of the existing game with the new data
        existingGame.name = updatedGame.name
        existingGame.overview = updatedGame.overview
        existingGame.launchDate = updatedGame.launchDate
        existingGame.studio = updatedGame.studio
        return repository.save(existingGame)
    }

    fun delete(id: Long) {
        val game = findById(id)
        repository.delete(game)
    }
}