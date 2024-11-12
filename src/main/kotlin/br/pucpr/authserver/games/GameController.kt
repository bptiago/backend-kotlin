package br.pucpr.authserver.games

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.games.requests.CreateGameRequest
import br.pucpr.authserver.games.requests.UpdateGameRequest
import br.pucpr.authserver.utils.SortDir
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/games")
class GameController (
    val gameService: GameService
) {
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("permitAll()")
    @PostMapping
    fun insert(@RequestBody @Valid gameRequest: CreateGameRequest) =
        gameService.insert(gameRequest.toGame(), gameRequest.fkStudioId)
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @GetMapping
    fun list(@RequestParam(required = false) sortDir: String?) =
        gameService.list(
            SortDir.getByName(sortDir) ?: throw BadRequestException("Parâmetro SORT inválido (deve ser ASC ou DESC)")
        )
            .let { ResponseEntity.ok().body(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Game> =
        gameService.getStudio(id)
            .let { ResponseEntity.ok(it) }

    @GetMapping("/name")
    fun searchWithName(@RequestParam(required = true) name: String) =
        gameService.searchWithName(name)
            .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "AuthServer")
    @PatchMapping("/{id}")
    @PreAuthorize("permitAll()")
    fun update(@PathVariable id: Long, @RequestBody @Valid gameRequest: UpdateGameRequest): ResponseEntity<Game> =
        gameService.update(id, gameRequest)
            .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "AuthServer")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        gameService.delete(id)
            .let { ResponseEntity.ok().build() }
}