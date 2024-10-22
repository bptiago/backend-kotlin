package br.pucpr.authserver.games

import br.pucpr.authserver.errors.BadRequestException
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
}