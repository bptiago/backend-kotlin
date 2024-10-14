package br.pucpr.authserver.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    @Query(
        "select u from User u " +
        " join u.roles r" +
        " where r.name = :role" +
        " order by u.name"
    )
    fun findByRole(role: String): List<User>
}
