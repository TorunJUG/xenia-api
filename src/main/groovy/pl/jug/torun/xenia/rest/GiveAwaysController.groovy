package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.rest.dto.GiveAwayResponse
import pl.jug.torun.xenia.rest.dto.GiveAwaysResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = "/event/{eventId}/giveaways",
    produces = [MediaType.APPLICATION_JSON_VALUE])
class GiveAwaysController {

    @Autowired
    EventRepository eventRepository

    @RequestMapping(method = RequestMethod.GET)
    GiveAwaysResponse getGiveAways(@PathVariable('eventId') long eventId) {
        def aways = eventRepository.getOne(eventId).giveAways
        return new GiveAwaysResponse(giveAways: aways.collect { new GiveAwayResponse(it) })
    }
}
