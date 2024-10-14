package br.pucpr.authserver.users

import br.pucpr.authserver.errors.BadRequestException
import br.pucpr.authserver.errors.NotFoundException
import br.pucpr.authserver.roles.RoleRepository
import br.pucpr.authserver.security.Jwt
import br.pucpr.authserver.users.responses.LoginResponse
import br.pucpr.authserver.users.responses.UserResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    val repository: UserRepository,
    val roleRepository: RoleRepository,
    val jwt: Jwt
) {
    fun insert(user: User): User {
        if (user.id != null)
            throw IllegalArgumentException("Usuário já inserido!")

        if (repository.findByEmail(user.email) != null)
            throw BadRequestException("Usuário com email ${user.email} já existe!")
        return repository.save(user)
    }

    fun list(sortDir: SortDir, role: String?): List<User> {
        if (role != null) {
            return repository.findByRole(role)
        } else {
            return when (sortDir) {
                SortDir.ASC -> repository.findAll()
                SortDir.DESC -> repository.findAll(Sort.by("id").reverse())
            }
        }
    }

    fun findByIdOrNull(id: Long) = repository.findByIdOrNull(id)

    fun delete(id: Long): User? {
        val user = repository.findByIdOrNull(id)
            ?: return null

        if (user.roles.any { it.name == "ADMIN" } &&
            repository.findByRole("ADMIN").size == 1) {
            throw BadRequestException("Não é possível excluir o último administrador!")
        }

        repository.delete(user)
        return user
    }

    fun update(id: Long, name: String): User? {
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("Usuário ${id} não encontrado!")
        if (user.name == name)
            return null
        user.name = name
        return repository.save(user)
    }

    fun addRole(id: Long, roleName: String): Boolean {
        val user = repository.findByIdOrNull(id)
            ?: throw NotFoundException("Usuário não encontrado")

        if (user.roles.any { it.name == roleName }) return false

        val role = roleRepository.findByName(roleName)
            ?: throw BadRequestException("Invalid role name!")

        user.roles.add(role)
        repository.save(user)
        return true
    }

    fun login(email: String, password: String): LoginResponse? {
        val user = repository.findByEmail(email) ?: return null
        if (user.password != password) return null
        log.info("User logged in. id={} name={}", user.id, user.name)
        return LoginResponse(
            token = jwt.createToken(user),
            user = UserResponse(user)
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}
