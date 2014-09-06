package pl.jug.torun.xenia.model

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository

import static org.assertj.core.api.Assertions.assertThat


@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@WebAppConfiguration
@IntegrationTest
class PrizeIntegrationTest {

    @Autowired
    PrizeRepository prizeRepository

    Prize prize = new Prize(name: 'Licencja na IntelliJIDEA', producer: 'JetBrains', sponsorName: 'JetBrains')

    @Test
    void shouldGenerateIdOnPersist() {
        //when:
        Prize persistedPrize = prizeRepository.save(prize)
        //then:
        assertThat(persistedPrize.id).isGreaterThan(0)
    }
}