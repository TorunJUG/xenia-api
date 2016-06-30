package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.rest.dto.DrawResponse
import pl.jug.torun.xenia.rest.dto.DrawsResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = "/event/{eventId}/giveaway/{giveAwayId}/draws",
    produces = [MediaType.APPLICATION_JSON_VALUE])
class DrawsController {

    @Autowired
    EventRepository eventRepository

    @RequestMapping(method = RequestMethod.GET)
    DrawsResponse getDraws(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId) {
        def event = eventRepository.getOne(eventId)
        def aways = event.giveAways
        return new DrawsResponse(draws: aways.find { it.id == giveAwayId }.draws.collect { new DrawResponse(it) })
    }
}
