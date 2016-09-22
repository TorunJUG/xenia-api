package pl.jug.torun.xenia.prizes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

@Stepwise
@DataJpaTest
@ContextConfiguration
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class PrizesControllerSpec extends Specification {

    @Autowired
    private PrizeRepository prizeRepository

    @Subject
    private PrizesController controller

    def setup() {
        controller = new PrizesController(prizeRepository)
    }

    def "should confirm that there are no prizes in the beginning"() {
        when:
        List<Prize> prizes = controller.listAll()

        then:
        prizes.empty
    }

    def "should create first prize"() {
        when:
        Prize prize = controller.create(new Prize("Test prize #1", "", false))

        then:
        prize.id > 0

        and:
        prizeRepository.count() == 1
    }

    def "should confirm there is first prize created"() {
        when:
        List<Prize> prizes = controller.listAll()

        then:
        prizes.size() == 1

        and:
        prizes.first().name == "Test prize #1"
    }

    def "should confirm that there is one active prize"() {
        when:
        List<Prize> prizes = controller.listActive()

        then:
        prizes.size() == 1

        and:
        prizes.first().name == "Test prize #1"
    }

    def "should confirm that there is no inactive prize out there"() {
        when:
        List<Prize> prizes = controller.listInactive()

        then:
        prizes.isEmpty()
    }

    def "should add second prize"() {
        when:
        Prize prize = controller.create(new Prize("Test prize #2", "", false))

        then:
        prize.id > 0

        and:
        prizeRepository.count() == 2
    }

    def "should confirm there are 2 prizes in general"() {
        when:
        List<Prize> prizes = controller.listAll()

        then:
        prizes.size() == 2
    }

    def "should confirm there are 2 active prizes"() {
        when:
        List<Prize> prizes = controller.listActive()

        then:
        prizes.size() == 2
    }

    def "should confirm there is still no inactive prize"() {
        when:
        List<Prize> prizes = controller.listInactive()

        then:
        prizes.isEmpty()
    }

    def "should throw an exception if user tries to disable prize that does not exist"() {
        when:
        controller.makeInactive(prizeRepository.findByName("Prize that does not exist"))

        then:
        thrown IllegalArgumentException
    }

    def "should disable first prize"() {
        when:
        Prize prize = controller.makeInactive(prizeRepository.findByName("Test prize #1"))

        then:
        prize.name == "Test prize #1"

        and:
        prize.inactive
    }

    def "should confirm there are still 2 prizes in general"() {
        when:
        List<Prize> prizes = controller.listAll()

        then:
        prizes.size() == 2
    }

    def "should confirm there is one active prize out there"() {
        when:
        List<Prize> prizes = controller.listActive()

        then:
        prizes.size() == 1

        and:
        prizes.first().name == "Test prize #2"
    }

    def "should confirm there is one inactive prize out there"() {
        when:
        List<Prize> prizes = controller.listInactive()

        then:
        prizes.size() == 1

        and:
        prizes.first().name == "Test prize #1"
    }

    def "should throw an exception if user tries to enable prize that does not exist"() {
        when:
        controller.makeActive(prizeRepository.findByName("Prize that does not exist"))

        then:
        thrown IllegalArgumentException
    }

    def "should make disabled prize active again"() {
        when:
        Prize prize = controller.makeActive(prizeRepository.findByName("Test prize #1"))

        then:
        !prize.inactive

        and:
        prize.name == "Test prize #1"
    }

    def "should confirm that there are 2 active prizes now"() {
        when:
        List<Prize> prizes = controller.listActive()

        then:
        prizes.size() == 2
    }

    def "should confirm there is no inactive prize now"() {
        when:
        List<Prize> prizes = controller.listInactive()

        then:
        prizes.isEmpty()
    }

    def "should throw an exception if user tries to update prize that does not exist"() {
        given:
        long nonExistingPrizeId = 1024L

        when:
        controller.update(nonExistingPrizeId, new Prize("Name updated", "", false))

        then:
        thrown IllegalArgumentException
    }

    def "should update existing prize"() {
        given:
        long existingPrizeId = prizeRepository.findByName("Test prize #1").id

        when:
        Prize prize = controller.update(existingPrizeId, new Prize("Test prize #1 UPDATED", "", false))

        then:
        prize.name == "Test prize #1 UPDATED"

        and:
        prize.id == existingPrizeId
    }

    def "should confirm that updated prize is available on the list"() {
        when:
        List<Prize> prizes = controller.listActive()

        then:
        prizes.find { it.name == "Test prize #1 UPDATED"} != null

        and:
        prizes.size() == 2
    }
}
