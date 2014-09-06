package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.rest.dto.DrawResponse
import pl.jug.torun.xenia.rest.dto.DrawsResponse
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.rest.dto.EventsResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/event/{eventId}/giveaway/{giveAwayId}/draws")
class DrawsController {

    @Autowired
    EventRepository eventRepository
    
    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    DrawsResponse getDraws(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId) {
        def event = eventRepository.getOne(eventId)

        def aways = event.giveAways
        return new DrawsResponse(draws: aways.find{it.id == giveAwayId}.draws.collect{new DrawResponse(it)})
        

    }
}
