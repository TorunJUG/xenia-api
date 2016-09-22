package pl.jug.torun.xenia.events

import groovy.transform.EqualsAndHashCode
import org.hibernate.annotations.ColumnDefault
import pl.jug.torun.xenia.meetup.Member

import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
@EqualsAndHashCode
final class Attendee {

    @EmbeddedId
    private Id id

    @ColumnDefault("false")
    boolean absent

    private Attendee() {}

    public Attendee(final Event event, final Member member) {
        this.id = new Id(event, member)
    }

    public Event getEvent() {
        return id?.event
    }

    public Member getMember() {
        return id?.member
    }

    @Override
    public String toString() {
        return "Attendee(id: ${id})".trim()
    }

    @Embeddable
    @EqualsAndHashCode
    static class Id implements Serializable {
        @ManyToOne
        protected Event event
        @ManyToOne
        protected Member member

        private Id() {}

        public Id(Event event, Member member) {
            this.event = event
            this.member = member
        }

        @Override
        public String toString() {
            return "Id(event: ${event}, member: ${member})".trim()
        }
    }
}