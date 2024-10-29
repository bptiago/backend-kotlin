package br.pucpr.authserver.security

import br.pucpr.authserver.users.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.jackson.io.JacksonDeserializer
import io.jsonwebtoken.jackson.io.JacksonSerializer
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
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

    fun extract(req: HttpServletRequest): Authentication? {
        try {
            val header = req.getHeader(AUTHORIZATION)
            if (header == null || !header.startsWith("Bearer ")) return null
            val token = header.removePrefix("Bearer ")
            val claims = Jwts.parser()
                .json(JacksonDeserializer(
                    mapOf(USER_FIELD to UserToken::class.java))
                )
                .verifyWith(Keys.hmacShaKeyFor(SECRET.toByteArray()))
                .build()
                .parseSignedClaims(token).payload
            //Aplicamos nossas próprias regras de segurança
            if (claims.issuer != ISSUER) {
                log.debug("Token rejected: $ISSUER != ${claims.issuer}")
                return null
            }
            return claims
                .get(USER_FIELD, UserToken::class.java)
                .toAuthentication()
        } catch (e: Throwable) {
            log.debug("Token rejected", e)
            return null
        }
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

        private fun UserToken.toAuthentication(): Authentication {
            val authorities = this.roles.map {
                SimpleGrantedAuthority("ROLE_$it") }
            return UsernamePasswordAuthenticationToken.authenticated(
                this, id, authorities)
        }

    }
}
