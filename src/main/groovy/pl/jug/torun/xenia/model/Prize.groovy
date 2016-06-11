package pl.jug.torun.xenia.model

import groovy.transform.AutoClone
import org.hibernate.annotations.ColumnDefault

import javax.persistence.*

/**
 * Created by mephi_000 on 06.09.14.
 */
@Entity
@AutoClone
class Prize {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Column(nullable = false, unique = true)
    String name

    @Column(nullable = true)
    String producer

    @Column(nullable = true)
    String imageUrl

    @Column(nullable = true)
    String sponsorName

    @ColumnDefault("false")
    boolean deleted = false
}
