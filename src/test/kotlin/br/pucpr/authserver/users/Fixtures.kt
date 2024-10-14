package br.pucpr.authserver.users

fun user(id: Long? = 1L,
         name: String = "John Doe",
         email: String = "john@authserver.com",
         password: String = "p@ssw0rd!"
) = User(
        id=id,
        name=name,
        email = email,
        password = password
)
