package pl.jug.torun.xenia.rest

import pl.jug.torun.xenia.dao.GiveAwayRepository
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeRequest
import spock.lang.Specification
import spock.lang.Unroll

class PrizeServiceSpec extends Specification {

    private static final long EXISTING_PRIZE_ID = 1024l
    private static final long NON_EXISTING_PRIZE_ID = 128l
    private static final Prize EXISTING_PRIZE = new Prize(id: EXISTING_PRIZE_ID, name: "Test", sponsorName: "unknown", producer: "Lorem ipsum LTD", imageUrl: null)

    final PrizeRepository prizeRepository = Mock(PrizeRepository)
    final GiveAwayRepository giveAwayRepository = Mock(GiveAwayRepository)
    final PrizeService service = new PrizeService(prizeRepository, giveAwayRepository)

    def setup() {
        prizeRepository.findOne(EXISTING_PRIZE_ID) >> EXISTING_PRIZE.clone()
        prizeRepository.findOne(NON_EXISTING_PRIZE_ID) >> null
        prizeRepository.save(_ as Prize) >> { return it[0] }
    }

    def "should return existing Prize by its id"() {
        when:
            def prize = service.get(EXISTING_PRIZE_ID)

        then:
            prize != null

        and:
            prize.id == EXISTING_PRIZE_ID
    }

    def "should return null if prize with given id does not exist"() {
        when:
            def prize = service.get(NON_EXISTING_PRIZE_ID)

        then:
            prize == null
    }

    def "should create a new prize from PrizeRequest object"() {
        given:
            PrizeRequest request = new PrizeRequest("Spring Boot in Action", "Manning", "unknown", "https://place.it/300x300")

        when:
            Prize prize = service.create(request)

        then:
            prize != null

        and:
            prize.id != null

        and:
            prize.name == request.name

        and:
            prize.producer == request.producer

        and:
            prize.sponsorName == request.sponsorName

        and:
            prize.imageUrl == request.imageUrl
    }

    def "should throw IllegalArgumentException if prize with given name already exists"() {
        setup:
            String prizeName = "Lorem ipsum dolor sit amet"
            prizeRepository.countByName(prizeName) >> 1
            PrizeRequest request = new PrizeRequest(name: prizeName)

        when:
            service.create(request)

        then:
            thrown IllegalArgumentException
    }

    @Unroll
    def "should update existing prize with given id and request #request"() {
        when:
            Prize updatedPrize = service.update(EXISTING_PRIZE_ID, new PrizeRequest(request))

        then:
            updatedPrize.id == EXISTING_PRIZE_ID

        and:
            updatedPrize.name == expectedPrize.name

        and:
            updatedPrize.producer == expectedPrize.producer

        and:
            updatedPrize.sponsorName == expectedPrize.sponsorName

        and:
            updatedPrize.imageUrl == expectedPrize.imageUrl

        where:
            request                                                             || expectedPrize
            [name: "Spring Boot in Action", sponsorName: "abc"]                 || new Prize(name: "Spring Boot in Action", sponsorName: "abc", producer: EXISTING_PRIZE.producer, imageUrl: EXISTING_PRIZE.imageUrl)
            [name: null, sponsorName: null, producer: null, imageUrl: null]     || EXISTING_PRIZE
            [sponsorName: "Something"]                                          || new Prize(name: EXISTING_PRIZE.name, sponsorName: "Something", producer: EXISTING_PRIZE.producer, imageUrl: EXISTING_PRIZE.imageUrl)
            [name: "a", sponsorName: "b", producer: "c", imageUrl: "d"]         || new Prize(name: "a", sponsorName: "b", producer: "c", imageUrl: "d")
    }

    def "should throw IllegalArgumentException if update request contains prize name that is already taken"() {
        setup:
            String name = "Test"
            prizeRepository.countByNameAndIdNot(name, EXISTING_PRIZE_ID) >> 1
            PrizeRequest prizeRequest = new PrizeRequest(name: name)

        when:
            service.update(EXISTING_PRIZE_ID, prizeRequest)

        then:
            thrown IllegalArgumentException
    }

    def "should delete Prize if it was not yet used by any giveaway"() {
        setup:
        giveAwayRepository.countByPrizeId(EXISTING_PRIZE_ID) >> 0

        when:
        service.delete(EXISTING_PRIZE_ID)

        then:
        1 * prizeRepository.delete(EXISTING_PRIZE_ID)
    }

    def "should mark Prize as deleted if it is used by at least one giveaway"() {
        setup:
        giveAwayRepository.countByPrizeId(EXISTING_PRIZE_ID) >> 2

        when:
        service.delete(EXISTING_PRIZE_ID)

        then:
        1 * prizeRepository.save({ Prize it ->
            it.id == EXISTING_PRIZE_ID
            it.deleted
        })
    }
}
