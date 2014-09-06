package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
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
    EventRepository eventRepository
    
    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    DrawResponse getDraw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId,@PathVariable('id') long id ) {
        return new DrawResponse(eventRepository.getOne(eventId).giveAways.find{it.id == giveAwayId}.draws{it.id == id})
    }


    @RequestMapping( method = RequestMethod.PUT, produces = ["application/json"], consumes = ["application/json"])
    Map draw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId) {
        def event = eventRepository.getOne(eventId)
        def giveAway = event.giveAways.find { it.id = giveAwayId }
        def confirmed = giveAway.draws.count { it.confirmed }
        if (confirmed < giveAway.amount) {
            def attendees = event.attendees
            def winner = attendees.get(new Random().nextInt(attendees.size()))
            def draw = new Draw(attendee: winner, confirmed: false, drawDate: LocalDateTime.now())
            giveAway.draws.add(draw)
            eventRepository.save(event)
            
            return [resourceUrl: "/event/${eventId}/giveaway/${giveAwayId}/draw/" + draw.id]
        }
        
        return [error: "No more prizes left!"]
        
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.PATCH, produces = ["application/json"], consumes = ["application/json"])
    Map updateDraw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId, @RequestBody DrawRequest drawRequest) {
        return ["resourceUrl": '/event/' + eventId + '/giveaway/' + giveAwayId + '/draw/1']
    }
}
