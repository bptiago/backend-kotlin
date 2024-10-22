package br.pucpr.authserver.games

import br.pucpr.authserver.games.requests.CreateGameRequest
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}