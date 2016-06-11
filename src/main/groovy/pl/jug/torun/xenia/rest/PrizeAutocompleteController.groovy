package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.PrizeRepository

/**
 * Created by dex
 */
@RestController
@RequestMapping(value = "/prize/autocomplete", produces = ["application/json"], method = RequestMethod.GET)
class PrizeAutocompleteController {
    
    @Autowired
    PrizeRepository prizeRepository

    @RequestMapping(value = "/name")
    Set<String> getName(@RequestParam("q") String name) {
        return prizeRepository.findAllByNameStartingWithIgnoreCase(name).collect {
            p -> p.name
        }.toSet()
    }

    @RequestMapping(value = '/sponsor')
    Set<String> getSponsor(@RequestParam("q") String sponsor) {
        return prizeRepository.findAllBySponsorNameStartingWithIgnoreCase(sponsor).collect {
            p -> p.sponsorName
        }.toSet()
    }
    @RequestMapping(value = '/producer')
    Set<String> getProducer(@RequestParam("q") String producer) {
        return prizeRepository.findAllByProducerStartingWithIgnoreCase(producer).collect {
            p -> p.producer
        }.toSet()
    }
     
}
