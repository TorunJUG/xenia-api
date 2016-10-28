package pl.jug.torun.xenia.draw

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.events.EventRepository
import pl.jug.torun.xenia.meetup.Member
import pl.jug.torun.xenia.meetup.MemberRepository
import pl.jug.torun.xenia.prizes.Prize
import pl.jug.torun.xenia.prizes.PrizeRepository
import spock.lang.Specification
import spock.lang.Subject

import java.util.concurrent.atomic.AtomicLong

@DataJpaTest
@ContextConfiguration
class DrawResultControllerSpec extends Specification {

    @Autowired
    private DrawResultRepository drawResultRepository

    @Autowired
    private EventRepository eventRepository

    @Autowired
    private MemberRepository memberRepository

    @Autowired
    private PrizeRepository prizeRepository

    @Autowired
    private GiveAwayRepository giveAwayRepository


    @Subject
    private DrawResultController controller

    private Event event

    private AtomicLong counter = new AtomicLong(0)

    def setup() {
        controller = new DrawResultController(drawResultRepository)
        event = eventRepository.save(new Event(counter.getAndIncrement(), "Test event", DateTime.parse("2016-10-10T20:00:00")))
    }

    def "should return empty list of draw results when no draw result was stored"() {
        when:
        List<DrawResult> results = controller.listDrawResults(event)

        then:
        results.empty
    }

    def "should return a list of existing draw results"() {
        given:
        drawResult(giveaway("Spring Boot in Action ebook", 2), member("John"))
        drawResult(giveaway("Spring Boot in Action ebook"), member("Mark Smith"))
        drawResult(giveaway("Software license", 3), member("Paul"))
        drawResult(giveaway("Software license"), member("Ann"))
        drawResult(giveaway("Software license"), member("Joe Doe"))

        when:
        List<DrawResult> results = controller.listDrawResults(event)

        then:
        results.size() == 5

        and:
        results.giveAway.prize.findAll { it.name == "Spring Boot in Action ebook" }.size() == 2

        and:
        results.giveAway.prize.findAll { it.name == "Software license" }.size() == 3

        and:
        results.member.name == ["John", "Mark Smith", "Paul", "Ann", "Joe Doe"]
    }

    def "should also return draw results grouped by giveaway id"() {
        given:
        drawResult(giveaway("Spring Boot in Action ebook", 2), member("John"))
        drawResult(giveaway("Spring Boot in Action ebook"), member("Mark Smith"))
        drawResult(giveaway("Software license", 3), member("Paul"))
        drawResult(giveaway("Software license"), member("Ann"))
        drawResult(giveaway("Software license"), member("Joe Doe"))

        when:
        Map<Long, List<String>> result = controller.listGroupedDrawResults(event)

        then:
        result == [
                (giveaway("Spring Boot in Action ebook").id): ["John", "Mark Smith"],
                (giveaway("Software license").id): ["Paul", "Ann", "Joe Doe"],
        ]
    }

    def "should allow to download draw result in CSV format"() {
        given:
        drawResult(giveaway("Spring Boot in Action ebook", 2), member("John", "john@example.com"))
        drawResult(giveaway("Spring Boot in Action ebook"), member("Mark Smith"))
        drawResult(giveaway("Software license", 3), member("Paul"))
        drawResult(giveaway("Software license"), member("Ann", "ann@example.com"))
        drawResult(giveaway("Software license"), member("Joe Doe"))
        MockHttpServletResponse response = new MockHttpServletResponse()

        when:
        controller.downloadCsv(event, response)

        then:
        response.getContentAsString().readLines() == [
                '"Member ID","Won prize","Member name","Member e-mail"',
                '1,"Spring Boot in Action ebook","John","john@example.com"',
                '2,"Spring Boot in Action ebook","Mark Smith",""',
                '3,"Software license","Paul",""',
                '4,"Software license","Ann","ann@example.com"',
                '5,"Software license","Joe Doe",""'
        ]
    }

    private Member member(String name, String email = "") {
        return memberRepository.findByName(name) ?: memberRepository.save(new Member(counter.getAndIncrement(), name, "", email))
    }

    private Prize prize(String name) {
        return prizeRepository.findByName(name) ?: prizeRepository.save(new Prize(name, "", false))
    }

    private GiveAway giveaway(String name, int amount = 1, boolean emailRequired = false) {
        return giveAwayRepository.findByPrizeAndEvent(prize(name), event) ?: giveAwayRepository.save(new GiveAway(prize(name), event, amount, emailRequired))
    }

    private DrawResult drawResult(GiveAway giveAway, Member member) {
        return drawResultRepository.save(new DrawResult(giveAway, member))
    }
}
