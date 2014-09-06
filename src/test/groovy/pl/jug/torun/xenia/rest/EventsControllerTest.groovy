package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.junit.Test
import org.springframework.test.util.ReflectionTestUtils
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.rest.dto.EventsResponse

import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Created by mephi_000 on 06.09.14.
 */
class EventsControllerTest {


    public static final Event EVENT1 = new Event(title: "Hackaton #1", startDate: LocalDateTime.now(),
            endDate: LocalDateTime.now().plusDays(1), updatedAt: LocalDateTime.now(), meetupId: 123123L)
    public static final Event EVENT2 = new Event(title: "Hackaton #2", startDate: LocalDateTime.now(),
            endDate: LocalDateTime.now().plusDays(1), updatedAt: LocalDateTime.now(), meetupId: 123123L)

    @Test
    public void "should return current events"() {
        def controllerUnderTest = new EventsController()
        def repositoryMock = mock(EventRepository.class)
        when(repositoryMock.findAll()).thenReturn([EVENT1, EVENT2])
        ReflectionTestUtils.setField(controllerUnderTest, "eventRepository", repositoryMock)
        
        
        assertThat(controllerUnderTest.getEvents().events).containsOnly(new EventResponse(EVENT1), new EventResponse(EVENT2))

    }
}
