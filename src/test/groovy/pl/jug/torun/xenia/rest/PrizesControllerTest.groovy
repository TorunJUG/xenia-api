package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizesResponse
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@IntegrationTest
class PrizesControllerTest extends Specification {

    @Autowired
    private PrizeRepository prizeRepository

    @Autowired
    private PrizesController controller

    @Shared
    private boolean initialized

    def setup() {
        if (!initialized) {
            prizeRepository.deleteAll()

            (1..10).each {
                prizeRepository.save(
                        new Prize(name: "${it}", deleted: it % 2 == 0)
                )
            }

            initialized = true
        }
    }

    def "should return all non-deleted prizes"() {
        when:
        PrizesResponse response = controller.getPrizes()

        then:
        response.prizes.size() == 5

        and:
        response.prizes.every { it.name.toLong() % 2l == 1l }
    }
}
