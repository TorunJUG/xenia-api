package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.model.Draw
import pl.jug.torun.xenia.rest.dto.DrawRequest
import pl.jug.torun.xenia.rest.dto.DrawResponse


/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/event/{eventId}/giveaway/{giveAwayId}/draw")
class DrawController {

    @Autowired
    DrawServiceInterface drawService

    @Autowired
    EventRepository eventRepository
    
    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    DrawResponse getDraw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId,@PathVariable('id') long id ) {
        def giveAway = eventRepository.getOne(eventId).giveAways.find { it.id == giveAwayId }
        return new DrawResponse(giveAway.draws.find{it.id == id})
    }


    @RequestMapping( method = RequestMethod.POST, produces = ["application/json"], consumes = ["application/json"])
    Map draw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId) {
            
       def draw = drawService.draw(eventId, giveAwayId)
        if (draw != null) {
            return [resourceUrl: "/event/${eventId}/giveaway/${giveAwayId}/draw/" + draw.id]
        } 
        
        return [error: "No more prizes left!"]
        
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.PATCH, produces = ["application/json"], consumes = ["application/json"])
    Map updateDraw(@PathVariable('id') long id, @PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId, @RequestBody DrawRequest drawRequest) {
        if (drawRequest.confirmed == true)
            drawService.confirmDraw(eventId, giveAwayId, id)
        return ["resourceUrl": "/event/$eventId/giveaway/$giveAwayId/draw/$id".toString()]
    }
}
