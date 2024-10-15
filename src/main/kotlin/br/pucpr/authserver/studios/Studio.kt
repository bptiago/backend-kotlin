package br.pucpr.authserver.studios

import br.pucpr.authserver.games.Game
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Entity
@Table(name = "tbStudio")
class Studio (
    @Id @GeneratedValue
    var id: Long? = null,
    @NotNull
    var name: String,
    var location: String,
    var creationDate: LocalDate,

    @OneToMany(cascade = [(CascadeType.ALL)])
    @JoinColumn(name = "fk_studio_id", referencedColumnName = "id")
    var games: MutableList<Game>
)