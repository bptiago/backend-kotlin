package br.pucpr.authserver.users

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties("security.admin")
data class AdminConfig @ConstructorBinding constructor(
    val email: String,
    val password: String,
    val name: String,
)
