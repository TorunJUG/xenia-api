package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.rest.dto.DrawRequest
import pl.jug.torun.xenia.rest.dto.DrawResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = "/event/{eventId}/giveaway/{giveAwayId}/draw",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE])
class DrawController {

    @Autowired
    DrawServiceInterface drawService

    @Autowired
    EventRepository eventRepository

    @RequestMapping(value = "", method = RequestMethod.POST)
    String draw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId) {
        def draw = drawService.draw(eventId, giveAwayId)
        if (draw != null) {
            return '{"resourceUrl": "' + "/event/${eventId}/giveaway/${giveAwayId}/draw/${draw.id}" + '"}'
        }
        return '{"error": "No more prizes left!"}'
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    DrawResponse getDraw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId,
        @PathVariable('id') long id) {
        def giveAway = eventRepository.getOne(eventId).giveAways.find { it.id == giveAwayId }
        return new DrawResponse(giveAway.draws.find { it.id == id })
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.PUT)
    String modifyDraw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId,
        @PathVariable('id') long drawId) {
        def draw = drawService.draw(drawId, eventId, giveAwayId)
        if (draw != null) {
            return '{"resourceUrl": "' + "/event/${eventId}/giveaway/${giveAwayId}/draw/${draw.id}" + '"}'
        }
        return '{"error": "No more prizes left!"}'
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.PATCH)
    Map updateDraw(@PathVariable('id') long id, @PathVariable('eventId') long eventId,
        @PathVariable('giveAwayId') long giveAwayId, @RequestBody DrawRequest drawRequest) {
        if (drawRequest.confirmed) {
            drawService.confirmDraw(eventId, giveAwayId, id)
        }
        return ["resourceUrl": "/event/$eventId/giveaway/$giveAwayId/draw/$id".toString()]
    }
}
