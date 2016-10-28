package pl.jug.torun.xenia.events

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.jug.torun.xenia.http.ClassPathResourceClientHttpRequestFactory
import pl.jug.torun.xenia.meetup.MeetupClientAware
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
class AttendeesSynchronizationServiceSpec extends Specification implements MeetupClientAware {

    @Autowired
    private MemberRepository memberRepository

    @Autowired
    private AttendeeRepository attendeeRepository

    @Autowired
    private EventRepository eventRepository

    @Subject
    private AttendeesSynchronizationService service

    @Shared
    private Event event


    def setup() {
        service = new AttendeesSynchronizationService(attendeeRepository, memberRepository, meetupClient)
        if (event == null) {
            event = eventRepository.save(new Event(1L, "Test event #1", DateTime.parse("2016-08-08T20:00:00")))
        }
    }

    def "should confirm that there are no attendees before synchronization process starts"() {
        when:
        long counter = attendeeRepository.count()

        then:
        counter == 0L
    }

    def "should synchronize attendees of given event"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees.json', 'application/json'))

        when:
        service.synchronizeLocalAttendeesWithRemoteService(event)

        then:
        attendeeRepository.findAllByEventId(event.id).size() == 5

        and:
        attendeeRepository.count() == 5
    }

    def "should confirm that Mark Spencer didnt pass his email address"() {
        when:
        Member member = memberRepository.findByName("Mark Spencer")

        then:
        member.email == ""
    }

    def "should synchronize again with the server"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees_updated.json', 'application/json'))

        when:
        service.synchronizeLocalAttendeesWithRemoteService(event)

        then:
        attendeeRepository.findAllByEventId(event.id).size() == 5

        and:
        attendeeRepository.count() == 5
    }


    def "should confirm that Mark Spencer passed his email address this time"() {
        when:
        Member member = memberRepository.findByName("Mark Spencer")

        then:
        member.email == "1235bd4f-32f0-4810-be7f-b8b2dfaddee1@mailinator.com"
    }
}
