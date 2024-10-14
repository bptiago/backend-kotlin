package br.pucpr.authserver.users

import br.pucpr.authserver.roles.Role
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name="tbUser")
class User(
    @Id @GeneratedValue
    var id: Long? = null,

    @NotNull
    var name: String,

    @NotNull
    @Column(unique=true)
    var email: String,

    @NotNull
    var password: String,

    @ManyToMany()
    @JoinTable(
        name = "UsersRole",
        joinColumns = [JoinColumn(name="idUser")],
        inverseJoinColumns = [JoinColumn(name="idRole")]
    )
    val roles: MutableSet<Role> = mutableSetOf()
)
