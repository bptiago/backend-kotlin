package br.pucpr.authserver.studios

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudioRepository : JpaRepository<Studio, Long> {
}