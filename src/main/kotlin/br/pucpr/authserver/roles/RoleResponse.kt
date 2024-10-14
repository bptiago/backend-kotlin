package br.pucpr.authserver.roles

data class RoleResponse(
    val name: String,
    val description: String
) {
    constructor(role: Role): this(
        name=role.name,
        description=role.description
    )
}
