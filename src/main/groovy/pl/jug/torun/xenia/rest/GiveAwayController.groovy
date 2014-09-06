package pl.jug.torun.xenia.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.GiveAwayResponse
import pl.jug.torun.xenia.rest.dto.GiveAwaysResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/event/{eventId}/giveaway")
class GiveAwayController {

    @RequestMapping(value = '/{id}', method = RequestMethod.GET, produces = ["application/json"])
    GiveAwayResponse getGiveAways() {
        return new GiveAwayResponse(id: 1, prizeId: 1, amount: 2)        
    }
    
    
}
