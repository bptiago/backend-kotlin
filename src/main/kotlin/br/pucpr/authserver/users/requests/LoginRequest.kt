package br.pucpr.authserver.users.requests

data class LoginRequest(
    val email: String? = null,
    val password: String? = null
)
