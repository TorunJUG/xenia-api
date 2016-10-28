package pl.jug.torun.xenia.events

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Subject

@DataJpaTest
@ContextConfiguration
class EventsControllerSpec extends Specification {

    @Autowired
    private EventRepository eventRepository

    private EventsSynchronizationService eventsSynchronizationService = Mock(EventsSynchronizationService)

    @Subject
    private EventsController controller

    def setup() {
        controller = new EventsController(eventRepository, eventsSynchronizationService)
    }

    def "should return empty list of events if no single event was created"() {
        when:
        List<Event> events = controller.listAll()

        then:
        events.size() == 0
    }

    def "should return list of events sorted by date descending"() {
        given:
        eventRepository.save(new Event(1, "Event #1", DateTime.parse("2016-11-24T18:00:00")))
        eventRepository.save(new Event(2, "Event #2", DateTime.parse("2016-12-01T18:00:00")))

        when:
        List<Event> events = controller.listAll()

        then:
        events.size() == 2

        and:
        events.first().id == 2

        and:
        events.first().name == "Event #2"

        and:
        events.last().id == 1

        and:
        events.last().name == "Event #1"
    }

    def "should call EventsSynchronizationService as a /refresh call"() {
        when:
        controller.refresh()

        then:
        1 * eventsSynchronizationService.synchronizeLocalEventsWithRemoteService()
    }

    def "should return null if event does not exist when calling for event details"() {
        given:
        long nonExistingEventId = 1024L

        when:
        Event event = controller.eventDetails(nonExistingEventId)

        then:
        event == null
    }

    def "should return existing event by its id"() {
        given:
        Event existingEvent = eventRepository.save(new Event(1, "Event #1", DateTime.parse("2016-11-24T18:00:00")))

        when:
        Event event = controller.eventDetails(1L)

        then:
        event == existingEvent
    }
}
