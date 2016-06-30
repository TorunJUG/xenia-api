package pl.jug.torun.xenia.model

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@IntegrationTest
class PrizeIntegrationTest extends Specification {

    @Autowired
    PrizeRepository prizeRepository

    def "Should generate id on persist"() {
        given:
            def prize = new Prize(name: 'Licencja na IntelliJIDEA', producer: 'JetBrains', sponsorName: 'JetBrains')
        when:
        def persistedPrize = prizeRepository.save(prize)
        then:
        persistedPrize.id > 0L
    }
}
