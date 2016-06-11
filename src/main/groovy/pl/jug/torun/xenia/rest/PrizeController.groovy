package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeRequest
import pl.jug.torun.xenia.rest.dto.PrizeResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = '/prize', produces = ['application/json'])
public class PrizeController {

    final PrizeServiceInterface prizeSerivce

    @Autowired
    PrizeController(PrizeServiceInterface prizeSerivce) {
        this.prizeSerivce = prizeSerivce
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    PrizeResponse get(@PathVariable('id') long id) {
        Prize prize = prizeSerivce.get(id)
        return new PrizeResponse(prize)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = ['application/json'])
    Map create(@RequestBody PrizeRequest request) {
        Prize prize = prizeSerivce.create(request)
        return [resourceUrl: "/prize/${prize?.id}".toString()]
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, consumes = ['application/json'])
    Prize update(@PathVariable('id') long id, @RequestBody PrizeRequest request){
        return prizeSerivce.update(id, request)
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable('id') long id){
        prizeSerivce.delete(id)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException)
    def handleIllegalArgumentException(final IllegalArgumentException e) {
        return [
                message: e.message
        ]
    }
}
