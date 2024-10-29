package br.pucpr.authserver.games

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.games.requests.UpdateGameRequest

import br.pucpr.authserver.studios.StudioRepository
import br.pucpr.authserver.utils.SortDir
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

        val studio = studioRepository.findByIdOrNull(studioId) ?: throw BadRequestException("Não há studio cadastrado com ID $studioId")
        studio.games.add(game)
        studioRepository.save(studio)
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
        existingGame.name = gameRequest.name
        existingGame.overview = gameRequest.overview
        existingGame.launchDate = gameRequest.launchDate
        return repository.save(existingGame)
    }

    // Todo: fazer um endpoint para atualizar o STUDIO do jogo

    fun delete(id: Long) {
        val game = getStudio(id)
        return repository.delete(game)
    }
}