package br.pucpr.authserver.roles

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class RoleService(
    val repository: RoleRepository
) {
    fun insert(role: Role) =
        repository.save(role)

    fun findAll(): List<Role> =
        repository.findAll(Sort.by("name"))

}
