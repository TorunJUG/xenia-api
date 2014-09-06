package pl.jug.torun.xenia.rest

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.GiveAwayRequest
import pl.jug.torun.xenia.rest.dto.GiveAwayResponse
import pl.jug.torun.xenia.rest.dto.GiveAwaysResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = "/event/{eventId}/giveaway", produces = ["application/json"])
class GiveAwayController {

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    GiveAwayResponse getGiveAways() {
        return new GiveAwayResponse(id: 1, prizeId: 1, amount: 2)        
    }

    @RequestMapping(method = RequestMethod.PUT, consumes = ["application/json"])
    Map putGiveAway(@PathVariable('eventId') long eventId, @RequestBody GiveAwayRequest request) {
        return [resourceUrl: "/events/" + eventId + "/giveaway/1231"]
    }
    
}
