package pl.jug.torun.xenia.model

import org.hibernate.annotations.Type
import org.joda.time.LocalDateTime
import org.springframework.data.annotation.CreatedDate

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Created by mephi_000 on 06.09.14.
 */
@Entity
class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id

    @Column(nullable = false)
    Long meetupId
    
    @Column(nullable = false)
    String title

    @OneToMany(cascade = CascadeType.ALL)
    List<GiveAway> giveAways
    
    @OneToMany
    List<Member> attendees
    
    @CreatedDate
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    LocalDateTime startDate
    
    @CreatedDate
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    LocalDateTime endDate

    @Column(nullable = false)
    LocalDateTime updatedAt
    
}
