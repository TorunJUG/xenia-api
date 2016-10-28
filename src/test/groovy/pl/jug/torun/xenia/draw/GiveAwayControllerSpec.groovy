package pl.jug.torun.xenia.draw

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.events.EventRepository
import pl.jug.torun.xenia.prizes.Prize
import pl.jug.torun.xenia.prizes.PrizeRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

import static pl.jug.torun.xenia.draw.GiveAwayController.CreateGiveAwayRequest
import static pl.jug.torun.xenia.draw.GiveAwayController.UpdateGiveAwayRequest

@Stepwise
@DataJpaTest
@ContextConfiguration
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class GiveAwayControllerSpec extends Specification {

    @Autowired
    private EventRepository eventRepository

    @Autowired
    private PrizeRepository prizeRepository

    @Autowired
    private GiveAwayRepository giveAwayRepository

    @Autowired
    private DrawResultRepository drawResultRepository

    @Subject
    private GiveAwayController controller

    @Shared
    private Event event

    @Shared
    private Prize licenseKey

    @Shared
    private Prize book

    def setup() {
        controller = new GiveAwayController(giveAwayRepository, prizeRepository, drawResultRepository)
        if (event == null) {
            event = eventRepository.save(new Event(1L, "Test event", DateTime.parse("2016-08-08T20:00:00")))
            licenseKey = prizeRepository.save(new Prize("License key", "", false))
            book = prizeRepository.save(new Prize("Book title", "", false))
        }
    }

    def "should confirm that there are no giveaways created for the event at the beginning"() {
        when:
        List<GiveAway> giveAways = controller.listAll(event)

        then:
        giveAways.isEmpty()
    }

    def "should also confirm that prize queue is empty when there is no giveaway created"() {
        when:
        List<GiveAway> giveAways = controller.prizesQueue(event)

        then:
        giveAways.isEmpty()
    }

    def "should create first giveaway"() {
        when:
        GiveAway giveAway = controller.create(event, new CreateGiveAwayRequest(prize: book.id, amount: 2))

        then:
        giveAway.id > 0

        and:
        giveAway.prize == book

        and:
        giveAway.event == event

        and:
        giveAway.amount == 2

        and:
        !giveAway.emailRequired
    }

    def "should confirm that there is one giveaway in the list"() {
        when:
        List<GiveAway> giveAways = controller.listAll(event)

        then:
        giveAways.size() == 1

        and:
        giveAways.first().prize == book
    }

    def "should also confirm that there are two prizes in the draw queue"() {
        when:
        List<GiveAway> giveAways = controller.prizesQueue(event)

        then:
        giveAways.prize == [book, book]
    }

    def "should create another giveaway"() {
        when:
        GiveAway giveAway = controller.create(event, new CreateGiveAwayRequest(prize: licenseKey.id, amount: 3))

        then:
        giveAway.id > 0

        and:
        giveAway.prize == licenseKey

        and:
        giveAway.event == event

        and:
        giveAway.amount == 3

        and:
        !giveAway.emailRequired
    }

    def "should confirm there are two different giveaways in the list"() {
        when:
        List<GiveAway> giveAways = controller.listAll(event)

        then:
        giveAways.size() == 2

        and:
        giveAways.first().prize == book

        and:
        giveAways.last().prize == licenseKey
    }

    def "should confirm there are 5 prizes in the prize queue"() {
        when:
        List<GiveAway> prizesQueue = controller.prizesQueue(event)

        then:
        prizesQueue.size() == 5

        and:
        prizesQueue.prize == [book, book, licenseKey, licenseKey, licenseKey]
    }

    def "should update giveaway"() {
        given:
        GiveAway giveAway = giveAwayRepository.findByPrizeAndEvent(book, event)

        when:
        GiveAway updated = controller.update(giveAway, new UpdateGiveAwayRequest(amount: 4, emailRequired: true))

        then:
        updated.id == giveAway.id

        and:
        updated.amount == 4

        and:
        updated.prize == giveAway.prize

        and:
        updated.emailRequired
    }

    def "should confirm that updated giveaway is on the list"() {
        when:
        List<GiveAway> giveAways = controller.listAll(event)

        then:
        giveAways.first().prize == book

        and:
        giveAways.first().emailRequired

        and:
        giveAways.first().amount == 4

        and:
        giveAways.size() == 2
    }

    def "should confirm that after update prize queue contains 7 prizes to draw"() {
        when:
        List<GiveAway> prizesQueue = controller.prizesQueue(event)

        then:
        prizesQueue.prize == [book, book, book, book, licenseKey, licenseKey, licenseKey]
    }

    def "should confirm which prizes in queue require email"() {
        when:
        List<GiveAway> prizesQueue = controller.prizesQueue(event)

        then:
        prizesQueue.emailRequired == ([true] * 4) + ([false] * 3)
    }

    def "should delete first giveaway"() {
        given:
        GiveAway giveAway = giveAwayRepository.findByPrizeAndEvent(book, event)

        when:
        controller.delete(giveAway)

        then:
        giveAwayRepository.count() == 1
    }

    def "should confirm that after deleting first giveaway list contains only one giveaway"() {
        when:
        List<GiveAway> giveAways = controller.listAll(event)

        then:
        giveAways.size() == 1

        and:
        giveAways.first().prize == licenseKey
    }

    def "should confirm that after deleting first giveaway there are 3 prizes in the prize queue"() {
        when:
        List<GiveAway> prizesQueue = controller.prizesQueue(event)

        then:
        prizesQueue.prize == [licenseKey, licenseKey, licenseKey]
    }
}
