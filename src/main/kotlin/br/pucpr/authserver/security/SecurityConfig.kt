package br.pucpr.authserver.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

@Configuration
@EnableWebSecurity
@SecurityScheme(
    name="AuthServer",
    type= SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
class SecurityConfig {
    @Bean
    fun mvc(introspector: HandlerMappingIntrospector) =
        MvcRequestMatcher.Builder(introspector)

    @Bean
    fun corsFilter() =
        CorsConfiguration().apply {
            addAllowedHeader("*")
            addAllowedMethod("*")
            addAllowedOrigin("*")
        }.let {
            UrlBasedCorsConfigurationSource().apply {
                registerCorsConfiguration("/**", it)
            }
        }.let {
            CorsFilter(it)
        }

    @Bean
    fun filterChain(security: HttpSecurity, mvc: MvcRequestMatcher.Builder):
            SecurityFilterChain =
        security
            .sessionManagement{ it.sessionCreationPolicy(STATELESS) }
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .headers { it.frameOptions { fo -> fo.disable() }}
            .authorizeHttpRequests { requests ->
                requests.anyRequest().permitAll()
            }.build()
}
