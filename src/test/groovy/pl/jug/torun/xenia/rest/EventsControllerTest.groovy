package pl.jug.torun.xenia.rest
import org.joda.time.LocalDateTime
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.service.EventsService
import spock.lang.Specification

import static pl.jug.torun.xenia.rest.EventsController.OK_RESPONSE
/**
 * Created by mephi_000 on 06.09.14.
 */
class EventsControllerTest extends Specification {

    EventsController controller
    EventsService eventsService

    def setup() {
        controller = new EventsController()
        eventsService = Mock(EventsService)
        controller.eventsService = eventsService
    }

    static final Event EVENT1 = new Event(title: "Hackaton #1", startDate: LocalDateTime.now(),
            endDate: LocalDateTime.now().plusDays(1), updatedAt: LocalDateTime.now(), meetupId: 123123L)
    static final Event EVENT2 = new Event(title: "Hackaton #2", startDate: LocalDateTime.now(),
            endDate: LocalDateTime.now().plusDays(1), updatedAt: LocalDateTime.now(), meetupId: 123123L)

    def "Should return current events"() {
        given:
            eventsService.findAll() >> [EVENT1, EVENT2]
        when:
            def eventsResponse = controller.getEvents()
        then:
            eventsResponse.events == [new EventResponse(EVENT1), new EventResponse(EVENT2)]
    }

    def "should refresh events"() {
        when:
            def refreshResponse = controller.refresh()
        then:
            1 * eventsService.refreshEvents()
            refreshResponse == OK_RESPONSE
    }

}
