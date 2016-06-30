package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeRequest
import pl.jug.torun.xenia.rest.dto.PrizeResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = '/prize',
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE])
public class PrizeController {

    @Autowired
    PrizeServiceInterface prizeService

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    Map create(@RequestBody PrizeRequest request) {
        Prize prize = prizeService.create(request)
        return [resourceUrl: "/prize/${prize?.id}".toString()]
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    PrizeResponse get(@PathVariable('id') long id) {
        Prize prize = prizeService.get(id)
        return new PrizeResponse(prize)
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    Prize update(@PathVariable('id') long id, @RequestBody PrizeRequest request) {
        return prizeService.update(id, request)
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    void delete(@PathVariable('id') long id) {
        prizeService.delete(id)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException)
    def handleIllegalArgumentException(final IllegalArgumentException e) {
        return [
            message: e.message
        ]
    }
}
