package pl.jug.torun.xenia.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@Controller
@RequestMapping("/prizes")
class PrizesController {
    
    @RequestMapping(method=RequestMethod.GET)
    PrizesResponse getPrizes() {
        
    }
    
    
    
}
