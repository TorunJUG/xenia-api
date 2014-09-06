package pl.jug.torun.xenia.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.OneToOne

/**
 * Created by mephi_000 on 06.09.14.
 */
@Entity
class GiveAway {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id
    @OneToOne
    Prize prize
    @Column(nullable = false)
    Integer amount
    @OneToMany
    List<Draw> draws
}
