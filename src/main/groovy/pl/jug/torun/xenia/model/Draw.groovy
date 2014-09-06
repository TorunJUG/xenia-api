package pl.jug.torun.xenia.model

import org.hibernate.annotations.Type
import org.joda.time.LocalDateTime
import org.springframework.data.annotation.CreatedDate

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne

/**
 * Created by mephi_000 on 06.09.14.
 */
@Entity
class Draw {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id
    @OneToOne
    Member attendee

    @CreatedDate
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    LocalDateTime drawDate

    @Column(nullable = false)
    boolean confirmed
}
