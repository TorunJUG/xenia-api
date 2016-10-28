package pl.jug.torun.xenia.events

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.meetup.Member
import pl.jug.torun.xenia.meetup.MemberRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@DataJpaTest
@ContextConfiguration
class AttendeesControllerSpec extends Specification {

    @Autowired
    private AttendeeRepository attendeeRepository

    @Autowired
    private MemberRepository memberRepository

    @Autowired
    private EventRepository eventRepository

    private AttendeesSynchronizationService attendeesSynchronizationService = Mock(AttendeesSynchronizationService)

    @Subject
    private AttendeesController controller

    @Shared
    private Event event

    def setup() {
        controller = new AttendeesController(attendeeRepository, attendeesSynchronizationService)
        event = eventRepository.save(new Event(1L, "Test event #1", DateTime.parse("2016-06-06T06:06:06")))
    }

    def "should return empty members list if given event has no attendees"() {

        when:
        List<Member> members = controller.listAll(event.id)

        then:
        members.empty
    }

    def "should return list of members that attend this even"() {
        setup:
        attendeeRepository.save(new Attendee(event, memberRepository.save(new Member(1L, "Joe Doe", "", ""))))
        attendeeRepository.save(new Attendee(event, memberRepository.save(new Member(2L, "Mark Smith", "", ""))))

        when:
        List<Member> members = controller.listAll(event.id)

        then:
        members.size() == 2

        and:
        members.find { it.name == "Joe Doe" }.id == 1

        and:
        members.find { it.name == "Mark Smith" }.id == 2
    }


    def "should call AttendeesSynchronizationService in refresh call"() {
        when:
        controller.refresh(event)

        then:
        1 * attendeesSynchronizationService.synchronizeLocalAttendeesWithRemoteService(event)
    }
}
