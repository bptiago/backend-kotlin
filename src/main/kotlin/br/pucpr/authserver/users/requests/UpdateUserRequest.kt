package br.pucpr.authserver.users.requests

import jakarta.validation.constraints.NotBlank

data class UpdateUserRequest(
    @field:NotBlank
    val name: String?
)
