package pl.jug.torun.xenia.rest

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.AttendeesResponse
import pl.jug.torun.xenia.rest.dto.GiveAwayResponse
import pl.jug.torun.xenia.rest.dto.GiveAwaysResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/event/{eventId}/giveaways")
class GiveAwaysController {

    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    GiveAwaysResponse getGiveAways() {
        return new GiveAwaysResponse(giveAways: [
                new GiveAwayResponse(prizeId: 1, amount: 2),
                new GiveAwayResponse(prizeId: 2, amount: 1)
                
        ])
        
    }
}
