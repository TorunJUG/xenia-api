package pl.jug.torun.xenia.events

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.jug.torun.xenia.meetup.Member
import pl.jug.torun.xenia.meetup.MemberRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

@Stepwise
@DataJpaTest
@ContextConfiguration
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AttendeeRepositorySpec extends Specification {

    @Subject
    @Autowired
    AttendeeRepository attendeeRepository

    @Autowired
    EventRepository eventRepository

    @Autowired
    MemberRepository memberRepository

    @Shared
    boolean initialized
    @Shared
    Member john = new Member(1, "John Doe", "", "")
    @Shared
    Member mark = new Member(2, "Mark Doe", "", "")
    @Shared
    Member paul = new Member(3, "Paul Doe", "", "")
    @Shared
    Event event = new Event(128, "Test Meetup", DateTime.parse("2016-08-29"))

    def setup() {
        if (!initialized) {
            memberRepository.save(john)
            memberRepository.save(mark)
            memberRepository.save(paul)
            eventRepository.save(event)
        }
    }

    def "should create first event attendee"() {
        given:
        Attendee attendee = new Attendee(event, john)

        when:
        attendeeRepository.save(attendee)

        then:
        attendeeRepository.count() == 1
    }

    def "should create second event attendee"() {
        given:
        Attendee attendee = new Attendee(event, mark)

        when:
        attendeeRepository.save(attendee)

        then:
        attendeeRepository.count() == 2
    }

    def "should confirm that John attends event"() {
        when:
        Attendee attendee = attendeeRepository.findOne(new Attendee.Id(event, john))

        then:
        attendee.member == john
    }

    def "should confirm that Mark attends event"() {
        when:
        Attendee attendee = attendeeRepository.findOne(new Attendee.Id(event, mark))

        then:
        attendee.member == mark
    }

    def "should return a list of all event attendees"() {
        when:
        List<Attendee> attendees = attendeeRepository.findAllByEventId(event.id)

        then:
        attendees.size() == 2
    }

    def "should return a list of all Mark's events"() {
        when:
        List<Attendee> attendees = attendeeRepository.findAllByMemberId(mark.id)

        then:
        attendees.size() == 1
    }

    def "should return a list of all John's events"() {
        when:
        List<Attendee> attendees = attendeeRepository.findAllByMemberId(john.id)

        then:
        attendees.size() == 1
    }

    def "should return an empty list of Paul's events"() {
        when:
        List<Attendee> attendees = attendeeRepository.findAllByMemberId(paul.id)

        then:
        attendees.empty
    }

    def "should remove all attendees"() {
        when:
        attendeeRepository.findAllByEventId(event.id).each {
            attendeeRepository.delete(it)
        }

        then:
        attendeeRepository.count() == 0
    }
}
