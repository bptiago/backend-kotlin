package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

@Component
class Jwt {
    fun createToken(user: User): String =
        UserToken(user).let {
            Jwts.builder()
                .json(JacksonSerializer())
                .signWith(Keys.hmacShaKeyFor(SECRET.toByteArray()))
                .issuedAt(utcNow().toDate())
                .expiration(utcNow().plusHours(
                    if (it.isAdmin) ADMIN_EXPIRE_HOURS else EXPIRE_HOURS
                ).toDate())
                .issuer(ISSUER)
                .subject(it.id.toString())
                .claim(USER_FIELD, it)
                .compact()
        }


    companion object {
        val log = LoggerFactory.getLogger(Jwt::class.java)
        val SECRET = "0e5582adfb7fa6bb770815f3c6b3534d311bd5fe"
        val EXPIRE_HOURS = 48L
        val ADMIN_EXPIRE_HOURS = 2L
        val ISSUER = "PUCPR AuthServer"
        val USER_FIELD = "User"

        private fun utcNow() =
            ZonedDateTime.now(ZoneOffset.UTC)
        private fun ZonedDateTime.toDate() =
            Date.from(this.toInstant())
    }
}
