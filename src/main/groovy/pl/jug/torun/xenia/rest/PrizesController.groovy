package pl.jug.torun.xenia.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.PrizeResponse
import pl.jug.torun.xenia.rest.dto.PrizesResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/prizes")
class PrizesController {

    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    PrizesResponse getPrizes() {
        return new PrizesResponse(prizes: [new PrizeResponse(id: 1, name: 'IntelliJ dla Zbyszka', producer: 'Jetbrains', 
                sponsorName: 'Grupa Allegro', imageUrl: 'http://rusticode.com/wp-content/uploads/2014/05/intellijidea-logo.png'),
                new PrizeResponse(id: 2, name: 'JArchitect', producer: 'Coder Gears',
                        sponsorName: 'Rule Financial', imageUrl: 'http://upload.wikimedia.org/wikipedia/commons/9/93/No-logo.svg')])
        
    }
}
