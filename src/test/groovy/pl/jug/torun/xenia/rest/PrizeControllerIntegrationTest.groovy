package pl.jug.torun.xenia.rest

import org.assertj.core.api.Condition
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeRequest
import pl.jug.torun.xenia.rest.dto.PrizeResponse

import static org.assertj.core.api.Assertions.assertThat

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@IntegrationTest
class PrizeControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    PrizeController prizeController

    @Autowired
    PrizeRepository prizeRepository

    Prize existingPrize

    @Before
    void setup() {
        existingPrize = prizeRepository.save(new Prize(name: 'Istniejaca nagroda', producer: 'Zbyszko', sponsorName: 'Szymon'))
    }

    @Test(expected = DataIntegrityViolationException.class)
    void shouldThrowAnExceptionIfPrizeWithNullNameIsTryingToBeAdded() {
        prizeController.create(new PrizeRequest(null, 'lorem', 'ipsum', null))
    }

    @Test
    void shouldReturnResourceUrlAfterCreatingNewPrize() {
        //when:
        Map response = prizeController.create(new PrizeRequest('Licencja IntelliJ IDEA', 'JetBrains', 'JetBrains', null))
        //then:
        response.resourceUrl =~ '/prize/[0-9]+'
    }

    @Test
    void shouldReturnPrizeResponseForRequestingExistingPrizeObject() {
        //when:
        PrizeResponse response = prizeController.get(existingPrize.id)
        //then:
        assertThat(response).is(createdFrom(existingPrize))
    }

    private static Condition<PrizeResponse> createdFrom(final Prize prize) {
        return new Condition<PrizeResponse>() {
            @Override
            boolean matches(PrizeResponse response) {
                return response != null &&
                        prize != null &&
                        response.id == prize.id &&
                        response.name == prize.name &&
                        response.imageUrl == prize.imageUrl &&
                        response.sponsorName == prize.sponsorName &&
                        response.producer == prize.producer
            }
        }
    }
}
