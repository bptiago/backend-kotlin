package br.pucpr.authserver.studios

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.games.requests.CreateStudioRequest
import br.pucpr.authserver.studios.requests.UpdateStudioRequest
import br.pucpr.authserver.studios.responses.CreateStudioResponse
import br.pucpr.authserver.utils.SortDir
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/studios")
class StudioController (
    val service: StudioService
) {
    @SecurityRequirement(name = "AuthServer")
    @PreAuthorize("permitAll()")
    @PostMapping
    fun insert(@RequestBody @Valid studioRequest: CreateStudioRequest) =
        service.insert(studioRequest.toStudio())
            .let { CreateStudioResponse(it) }
            .let { ResponseEntity.status(CREATED).body(it) }

    @GetMapping
    fun list(@RequestParam(required = false) sortDir: String?) =
        service.list(
            SortDir.getByName(sortDir) ?: throw BadRequestException("Parâmetro SORT inválido (deve ser ASC ou DESC)")
        )
            .let { ResponseEntity.ok().body(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<Studio> =
        service.getStudio(id)
            .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "AuthServer")
    @PatchMapping("/{id}")
    @PreAuthorize("permitAll()")
    fun update(@PathVariable id: Long, @RequestBody @Valid studioRequest: UpdateStudioRequest): ResponseEntity<Studio> =
        service.update(id, studioRequest)
            .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "AuthServer")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id)
            .let { ResponseEntity.ok().build() }
}