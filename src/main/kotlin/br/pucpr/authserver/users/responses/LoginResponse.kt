package br.pucpr.authserver.users.responses

data class LoginResponse(
    val token: String,
    val user: UserResponse
)
