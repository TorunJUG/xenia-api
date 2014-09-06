package pl.jug.torun.xenia.rest

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.jug.torun.xenia.rest.dto.PrizeResponse;
import pl.jug.torun.xenia.rest.dto.PrizesResponse
import pl.jug.torun.xenia.rest.dto.PutPrizeRequest;

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/prize")
public class PrizeController {

    @RequestMapping(value = '/{id}', method = RequestMethod.GET, produces = ["application/json"])
    PrizeResponse getPrize(@PathVariable('id') int id) {
        return new PrizeResponse(id: id, name: 'IntelliJ dla Zbyszka', producer: 'Jetbrains',
                sponsorName: 'Grupa Allegro', imageUrl: 'http://rusticode.com/wp-content/uploads/2014/05/intellijidea-logo.png')

    }

    @RequestMapping( method = RequestMethod.PUT, produces = ["application/json"], consumes = ["application/json"])
    Map insertPrize(@RequestBody PutPrizeRequest request) {
        return ["resourceUrl": "/prizes/12"]
    }
}
