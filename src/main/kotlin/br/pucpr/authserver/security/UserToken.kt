package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import com.fasterxml.jackson.annotation.JsonIgnore

data class UserToken(
    val id: Long,
    val name: String,
    val roles: Set<String>
) {
    constructor(): this(0, "", setOf())
    constructor(user: User) : this(
        id = user.id ?: -1L,
        name = user.name,
        roles = user.roles.map { it.name }.toSortedSet()
    )

    @get:JsonIgnore
    val isAdmin: Boolean get() = "ADMIN" in roles
}
