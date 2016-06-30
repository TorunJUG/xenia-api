package pl.jug.torun.xenia.model.meetup

import groovy.transform.EqualsAndHashCode
import pl.jug.torun.xenia.model.Member

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne

/**
 * Created by mephi_000 on 2014-09-17.
 */
@Entity
@EqualsAndHashCode
class MeetupMember {
    @Id
    long id

    @OneToOne(cascade = CascadeType.ALL)
    Member member
}
