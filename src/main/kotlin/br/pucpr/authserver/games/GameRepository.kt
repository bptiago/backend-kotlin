package br.pucpr.authserver.games

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Game>
}