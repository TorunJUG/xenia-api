package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeResponse
import pl.jug.torun.xenia.rest.dto.PrizeRequest

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = '/prize', produces = ['application/json'], consumes = ['application/json'])
public class PrizeController {

    @Autowired
    PrizeRepository prizeRepository

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    PrizeResponse get(@PathVariable('id') long id) {
        Prize prize = prizeRepository.getOne(id)
        return new PrizeResponse(prize)
    }

    @RequestMapping(method = RequestMethod.POST)
    Map create(@RequestBody PrizeRequest request) {
        Prize prize = request.toPrize()
        prize = prizeRepository.save(prize)
        return [resourceUrl: "/prize/${prize?.id}".toString()]
    }
}
