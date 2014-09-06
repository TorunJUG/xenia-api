package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.EventResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping('/event')
class EventController {

    @RequestMapping(value = '/{id}', method = RequestMethod.GET, produces = ["application/json"])
    EventResponse getResponse(@PathVariable('id') int id) {
        return new EventResponse(id: id, title: "Hackathon", startDate: LocalDateTime.now(),
                endDate: LocalDateTime.now().plusHours(1))
    }
}
