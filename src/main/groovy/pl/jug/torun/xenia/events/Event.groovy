package pl.jug.torun.xenia.events

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type
import org.joda.time.DateTime

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Meetup event class.
 */
@Entity
@ToString(includePackage = false, includeNames = true)
@EqualsAndHashCode
final class Event {
    @Id
    long id

    String name

    @JsonProperty("time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    DateTime startDateTime

    private Event() {}

    public Event(long id, String name, DateTime startDateTime) {
        this.id = id
        this.name = name
        this.startDateTime = startDateTime
    }
}
