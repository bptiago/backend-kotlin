package br.pucpr.authserver.roles

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern


data class CreateRoleRequest(
    @field:Pattern(regexp = "^[A-Z][0-9A-Z]*$")
    val name: String?,

    @field:NotBlank
    val description: String?
) {
    fun toRole() = Role(
        name=name!!,
        description=description!!
    )
}
