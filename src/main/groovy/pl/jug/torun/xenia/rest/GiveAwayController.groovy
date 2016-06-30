package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.dao.GiveAwayRepository
import pl.jug.torun.xenia.rest.dto.GiveAwayRequest
import pl.jug.torun.xenia.rest.dto.GiveAwayResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = "/event/{eventId}/giveaway",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE])
class GiveAwayController {

    @Autowired
    EventRepository eventRepository

    @Autowired
    GiveAwayRepository giveAwayRepository

    @Autowired
    GiveAwayServiceInterface giveAwayService

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    GiveAwayResponse getGiveAway(@PathVariable("eventId") long eventId, @PathVariable('id') long id) {
        def giveAway = giveAwayRepository.getOne(id)
        giveAway.amount > giveAway.draws.findAll { it.confirmed }.size()
        return new GiveAwayResponse(giveAway)
    }

    @RequestMapping(method = RequestMethod.POST)
    String putGiveAway(@PathVariable('eventId') long eventId, @RequestBody GiveAwayRequest request) {
        def giveAway = giveAwayService.saveGiveAway(eventId, request)
        return '{"resourceUrl": "' + "/event/$eventId/giveaway/$giveAway.id" + '"}'
    }
}
