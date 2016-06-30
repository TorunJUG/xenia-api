package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.rest.dto.PrizeResponse
import pl.jug.torun.xenia.rest.dto.PrizesResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = "/prizes",
    produces = [MediaType.APPLICATION_JSON_VALUE])
class PrizesController {

    @Autowired
    PrizeRepository prizeRepository

    @RequestMapping(method = RequestMethod.GET)
    PrizesResponse getPrizes() {
        return new PrizesResponse(prizes: prizeRepository.findAllByDeleted(false).collect { new PrizeResponse(it) })
    }
}
