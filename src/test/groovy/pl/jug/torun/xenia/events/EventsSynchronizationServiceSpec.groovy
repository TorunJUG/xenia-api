package pl.jug.torun.xenia.events

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.jug.torun.xenia.http.ClassPathResourceClientHttpRequestFactory
import pl.jug.torun.xenia.meetup.MeetupClientAware
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

@Stepwise
@DataJpaTest
@ContextConfiguration
@Transactional(propagation = Propagation.NOT_SUPPORTED) // <- prevents cleaning db after every test case
class EventsSynchronizationServiceSpec extends Specification implements MeetupClientAware {

    @Autowired
    EventRepository eventRepository

    @Subject
    EventsSynchronizationService service

    def setup() {
        service = new EventsSynchronizationService(eventRepository, meetupClient)
    }

    def "should confirm that there are no events synchronized in the beginning"() {
        when:
        long eventsCounter = eventRepository.count()

        then:
        eventsCounter == 0L
    }

    def "should synchronize events for the first time"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/01_events.json', 'application/json'))

        when:
        service.synchronizeLocalEventsWithRemoteService()

        then:
        eventRepository.count() == 38

        and:
        eventRepository.findAll().first().name == '1. spotkanie Toruń JUG'

        and:
        eventRepository.findAll().last().name == 'JDD 2016 - Ticket Raffle'
    }

    def "should remove one event before repeating synchronization"() {
        when:
        println eventRepository.count()
        eventRepository.delete(175597572L)

        then:
        eventRepository.count() == 37
    }

    def "should synchronize again and revert removed event"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/01_events.json', 'application/json'))

        when:
        service.synchronizeLocalEventsWithRemoteService()

        then:
        eventRepository.count() == 38

        and:
        eventRepository.findOne(175597572L).name == '2. spotkanie Toruń JUG'
    }

    def "should synchronized changes made to remote events"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/01_events_updated.json', 'application/json'))

        when:
        service.synchronizeLocalEventsWithRemoteService()

        then:
        eventRepository.findOne(175597572L).name == '2. spotkanie Toruń JUG - updated name'
    }
}