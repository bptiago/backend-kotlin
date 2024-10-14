package br.pucpr.authserver

import br.pucpr.authserver.roles.Role
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.users.AdminConfig
import br.pucpr.authserver.users.User
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.PropertySource
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

@Component
@PropertySource("classpath:security.properties")
class Bootstrapper(
    val roleRepository: RoleRepository,
    val userRepository: UserRepository,
    val adminConfig: AdminConfig
): ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val adminRole = roleRepository.findByName("ADMIN")
            ?: roleRepository.save(Role(
                name="ADMIN",
                description = "System Administrator")
            ).also {
                roleRepository.save(Role(
                    name="USER",
                    description = "Premium User")
                )
                log.info("ADMIN and USER roles created!")
            }
        if (userRepository.findByRole("ADMIN").isEmpty()) {
            val admin = User(
                email=adminConfig.email,
                password=adminConfig.password,
                name=adminConfig.name
            )
            admin.roles.add(adminRole)
            userRepository.save(admin)
            log.info("ADMIN user created!")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Bootstrapper::class.java)
    }
}
