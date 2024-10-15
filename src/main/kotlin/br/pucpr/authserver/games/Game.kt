package br.pucpr.authserver.games

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
@Table(name = "tbGame")
class Game (
    @Id @GeneratedValue
    var id: Long? = null,

    @NotNull
    var name: String,

    @NotNull
    var overview: String,

    @NotNull
    var launchDate: LocalDate,

    @NotNull
    var studio: String,

)