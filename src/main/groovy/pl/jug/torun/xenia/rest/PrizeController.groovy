package pl.jug.torun.xenia.rest

import org.h2.jdbc.JdbcSQLException
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeResponse
import pl.jug.torun.xenia.rest.dto.PrizeRequest


/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping(value = '/prize', produces = ['application/json'])
public class PrizeController {

    @Autowired
    PrizeRepository prizeRepository

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    PrizeResponse get(@PathVariable('id') long id) {
        Prize prize = prizeRepository.getOne(id)
        return new PrizeResponse(prize)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST, consumes = ['application/json'])
    Map create(@RequestBody PrizeRequest request) {

        if (prizeRepository.countByName(request.name) > 0) {
            throw new IllegalArgumentException("Prize with name '${request.name}' already exists")
        }

        Prize prize = request.toPrize()
        prize = prizeRepository.save(prize)
        return [resourceUrl: "/prize/${prize?.id}".toString()]
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.PUT, consumes = ['application/json'])
    Prize update(@PathVariable('id') long id, @RequestBody PrizeRequest request){
        Prize prize = prizeRepository.findOne(id)
        
        if (prizeRepository.countByNameAndIdNot(request.name,id) > 0) {
            throw new IllegalArgumentException("Prize with name '${request.name}' already exists")
        }
        
        prize.name = request.name ?: prize.name
        prize.producer = request.producer ?: prize.producer
        prize.imageUrl = request.imageUrl ?: prize.imageUrl
        prize.sponsorName = request.sponsorName ?: prize.sponsorName
        
        return prizeRepository.save(prize)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException)
    def handleIllegalArgumentException(final IllegalArgumentException e) {
        return [
                message: e.message
        ]
    }
    
    
}
