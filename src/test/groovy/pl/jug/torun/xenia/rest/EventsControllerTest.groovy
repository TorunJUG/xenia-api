package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.junit.Before
import org.junit.Test
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.rest.dto.EventsResponse
import pl.jug.torun.xenia.service.EventsService

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.*
import static pl.jug.torun.xenia.rest.EventsController.OK_RESPONSE

/**
 * Created by mephi_000 on 06.09.14.
 */
class EventsControllerTest {

    private EventsController controller
    private EventsService eventsService

    @Before
    public void setUp() throws Exception {
        controller = new EventsController()
        eventsService = mock(EventsService.class)
        controller.eventsService = eventsService
    }

    public static final Event EVENT1 = new Event(title: "Hackaton #1", startDate: LocalDateTime.now(),
            endDate: LocalDateTime.now().plusDays(1), updatedAt: LocalDateTime.now(), meetupId: 123123L)
    public static final Event EVENT2 = new Event(title: "Hackaton #2", startDate: LocalDateTime.now(),
            endDate: LocalDateTime.now().plusDays(1), updatedAt: LocalDateTime.now(), meetupId: 123123L)

    @Test
    public void "should return current events"() {
        when(eventsService.findAll()).thenReturn([EVENT1, EVENT2])

        EventsResponse eventsResponse = controller.getEvents()

        verify(eventsService).findAll()
        assertThat(eventsResponse.events).containsOnly(new EventResponse(EVENT1), new EventResponse(EVENT2))
    }

    @Test
    public void "should refresh events"() throws Exception {
        String refreshResponse = controller.refresh()

        verify(eventsService).refreshEvents()
        assertThat(refreshResponse).is(OK_RESPONSE)
    }

}
