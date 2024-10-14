package br.pucpr.authserver.users

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.users.requests.CreateUserRequest
import br.pucpr.authserver.users.requests.LoginRequest
import br.pucpr.authserver.users.requests.UpdateUserRequest
import br.pucpr.authserver.users.responses.LoginResponse
import br.pucpr.authserver.users.responses.UserResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    val service: UserService,
) {
    @PostMapping
    fun insert(@RequestBody @Valid user: CreateUserRequest) =
        service.insert(user.toUser())
            .let { UserResponse(it) }
            .let { ResponseEntity.status(CREATED).body(it) }


    @GetMapping
    fun list(
        @RequestParam(required = false) sortDir: String?,
        @RequestParam(required = false) role: String?
    ) =
        service.list(
            sortDir = SortDir.getByName(sortDir) ?:
                throw BadRequestException("Invalid sort dir!"),
            role=role
        )
        .map { UserResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long) =
        service.findByIdOrNull(id)
            ?.let { UserResponse(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> =
        service.delete(id)
            ?.let { ResponseEntity.ok().build() }
            ?: ResponseEntity.notFound().build()

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid updateUserRequest: UpdateUserRequest
    ): ResponseEntity<UserResponse> =
        service.update(id, updateUserRequest.name!!)
            ?.let { UserResponse(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()

    @PutMapping("/{id}/roles/{role}")
    fun grant(
        @PathVariable id: Long,
        @PathVariable role: String
    ): ResponseEntity<Void> =
    if (service.addRole(id, role.uppercase()))
        ResponseEntity.ok().build()
    else
        ResponseEntity.status(NO_CONTENT).build()

    @PostMapping("/login")
    fun login(@Valid @RequestBody login: LoginRequest) =
        service.login(login.email!!, login.password!!)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
}
