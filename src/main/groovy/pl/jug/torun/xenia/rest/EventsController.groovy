package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.rest.dto.EventsResponse
import pl.jug.torun.xenia.service.EventsService

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/events")
class EventsController {

    @Autowired
    EventsService eventsService

    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    EventsResponse getEvents() {
        return new EventsResponse(events: eventsService.findAll().collect {
            event -> new EventResponse(event)
        })
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = ["application/json"])
    String refresh() {
        eventsService.refreshEvents()
        return '{"status":"OK"}'
    }

}
