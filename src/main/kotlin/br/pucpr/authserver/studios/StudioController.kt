package br.pucpr.authserver.studios

import br.pucpr.authserver.games.requests.CreateStudioRequest
import br.pucpr.authserver.studios.responses.CreateStudioResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/studios")
class StudioController (
    val service: StudioService
) {
    @PostMapping
    fun insert(@RequestBody @Valid studioRequest: CreateStudioRequest) =
        service.insert(studioRequest.toStudio())
            .let { CreateStudioResponse(it) }
            .let { ResponseEntity.status(CREATED).body(it) }

    @GetMapping
    fun getStudio(@RequestParam id:Long) =
        service.getStudio(id).let {
            ResponseEntity.ok(it)
        }
}