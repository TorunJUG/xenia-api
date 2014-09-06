package pl.jug.torun.xenia.rest

import org.joda.time.Instant
import org.joda.time.LocalDateTime
import org.joda.time.Minutes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.rest.dto.EventsResponse
import pl.jug.torun.xenia.rest.dto.PrizesResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/events")
class EventsController {
    
    @Autowired
    EventRepository eventRepository

    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    EventsResponse getEvents() {
        return new EventsResponse(events: [
                new EventResponse(id: 1, title: "Hackathon", startDate: LocalDateTime.now(),
                        endDate: LocalDateTime.now().plusHours(1)), 
                new EventResponse(id: 2, title: "Hackathon #2", startDate: LocalDateTime.now(),
                        endDate: LocalDateTime.now().plusHours(1))])

    }
    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = ["application/json"])
    String refresh() {
        eventRepository.save(new Event(title: "Hackaton #1", startDate: LocalDateTime.now(),
                endDate: LocalDateTime.now().plusDays(1), updatedAt: LocalDateTime.now()));
        return "ok"
    }
    
}
