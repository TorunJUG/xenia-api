package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeRequest
import spock.lang.Specification

import javax.transaction.Transactional

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@IntegrationTest
@Transactional
class PrizeControllerIntegrationTest extends Specification {

    @Autowired
    PrizeController prizeController

    @Autowired
    PrizeRepository prizeRepository

    void setup() {
        prizeRepository.deleteAll()
    }

    def "Should throw an exception if prize with null name is trying to be added"() {
        given:
            def prizeRequest = new PrizeRequest(null, 'lorem', 'ipsum', null)
        when:
            prizeController.create(prizeRequest)
        then:
            thrown(DataIntegrityViolationException)
    }

    def "Should return resource url after creating new prize"() {
        given:
            def prizeRequest = new PrizeRequest('Licencja IntelliJ IDEA', 'JetBrains', 'JetBrains', null)
        when:
            def response = prizeController.create(prizeRequest)
        then:
            response.resourceUrl =~ '/prize/[0-9]+'
    }

    def "Should return prize response for requesting existing prize object"() {
        given:
            def existingPrize = prizeRepository.save(new Prize(name: 'Istniejaca nagroda', producer: 'Zbyszko', sponsorName: 'Szymon'))

        when:
            def response = prizeController.get(existingPrize.id)
        then:
            response.every {
                it.id == existingPrize.id &&
                        it.name == existingPrize.name &&
                        it.imageUrl == existingPrize.imageUrl &&
                        it.sponsorName == existingPrize.sponsorName &&
                        it.producer == existingPrize.producer
            }
    }

}
