package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.DrawRequest
import pl.jug.torun.xenia.rest.dto.DrawResponse


/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/event/{eventId}/giveaway/{giveAwayId}/draw")
class DrawController {


    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    DrawResponse getDraw() {
        return new DrawResponse(id: 1, attendeeId: 1, confirmed: false, drawDate: LocalDateTime.now())
    }


    @RequestMapping( method = RequestMethod.PUT, produces = ["application/json"], consumes = ["application/json"])
    Map draw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId) {
        return ["resourceUrl": '/event/' + eventId + '/giveaway/' + giveAwayId + '/draw/1']
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.PATCH, produces = ["application/json"], consumes = ["application/json"])
    Map updateDraw(@PathVariable('eventId') long eventId, @PathVariable('giveAwayId') long giveAwayId, @RequestBody DrawRequest drawRequest) {
        return ["resourceUrl": '/event/' + eventId + '/giveaway/' + giveAwayId + '/draw/1']
    }
}
