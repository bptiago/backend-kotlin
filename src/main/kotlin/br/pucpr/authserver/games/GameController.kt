package br.pucpr.authserver.games

import br.pucpr.authserver.games.requests.CreateGameRequest
import br.pucpr.authserver.games.requests.UpdateGameRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController (
    val gameService: GameService
) {
    @PostMapping
    fun insert(@RequestBody @Valid gameRequest: CreateGameRequest) =
        gameService.insert(gameRequest.toGame(), gameRequest.fkStudioId)
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    fun list() =
        gameService.list()
            .let { ResponseEntity.ok().body(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Game> =
        gameService.getStudio(id)
            .let { ResponseEntity.ok(it) }

    @PatchMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid gameRequest: UpdateGameRequest): ResponseEntity<Game> =
        gameService.update(id, gameRequest)
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        gameService.delete(id)
            .let { ResponseEntity.ok().build() }
}