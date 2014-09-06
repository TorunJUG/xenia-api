package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
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

    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    DrawsResponse getDraws() {
        return new DrawsResponse(draws: [
                new DrawResponse(id: 1, attendeeId: 1,confirmed: true, drawDate: LocalDateTime.now()),
                new DrawResponse(id: 2, attendeeId: 12,confirmed: false, drawDate: LocalDateTime.now())
        ])

    }
}
