package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.rest.dto.AllDrawsResponse
import pl.jug.torun.xenia.rest.dto.EventResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping('/event')
class EventController {
    
    @Autowired
    EventRepository eventRepository


    @Autowired
    DrawServiceInterface drawService

    @RequestMapping(value = '/{id}', method = RequestMethod.GET, produces = ["application/json"])
    EventResponse getResponse(@PathVariable('id') long id) {
        return new EventResponse(eventRepository.getOne(id))
    }

    @RequestMapping(value = '/{id}/all-draws', method = RequestMethod.POST, produces = ["application/json"])
    String getDraw(@PathVariable('id') long eventId) {
        drawService.draw(eventId)
        return "{\"status\": \"ok\"}"
    }
}
