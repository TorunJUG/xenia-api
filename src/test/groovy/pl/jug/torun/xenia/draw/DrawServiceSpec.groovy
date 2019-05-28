package pl.jug.torun.xenia.draw

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.events.*
import pl.jug.torun.xenia.meetup.Member
import pl.jug.torun.xenia.meetup.MemberRepository
import pl.jug.torun.xenia.prizes.Prize
import pl.jug.torun.xenia.prizes.PrizeRepository
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject

import java.util.concurrent.atomic.AtomicLong

import static org.apache.commons.lang.StringUtils.isNotBlank

@Stepwise
@DataJpaTest
@ContextConfiguration
class DrawServiceSpec extends Specification {

    private static final int RANDOM_TESTS_RUN_LIMIT = 50

    @Autowired
    private DrawResultRepository drawResultRepository

    @Autowired
    private AttendeeRepository attendeeRepository

    @Autowired
    private MemberRepository memberRepository

    @Autowired
    private GiveAwayRepository giveAwayRepository

    @Autowired
    private EventRepository eventRepository

    @Autowired
    private PrizeRepository prizeRepository

    @Subject
    private DrawService drawService

    private GiveAwayController giveAwayController

    private Event event

    private AtomicLong counter = new AtomicLong(0L)

    def setup() {
        drawService = new DrawService(drawResultRepository, new SkippedGiveAwayContainer(), attendeeRepository, memberRepository)
        giveAwayController = new GiveAwayController(giveAwayRepository, prizeRepository, drawResultRepository)
        event = event("Test event", DateTime.parse("2015-04-02T20:00:00"))
    }

    def "should throw an exception if giveaway amount is equal 0"() {
        setup:
        GiveAway giveAway = giveAwayWithAmount(0)

        when:
        drawService.drawWinnerCandidate(giveAway)

        then:
        thrown IllegalStateException
    }

    def "should throw an exception if no one attended the event"(){
        setup:
        GiveAway giveAway = giveAwayWithAmount(1)

        when:
        drawService.drawWinnerCandidate(giveAway)

        then:
        thrown IllegalStateException
    }

    def "should confirm that giveaway that requires email can be drawn by attendees with email only"() {
        setup:
        GiveAway giveAway = giveAwayWithPrizeAndAmount("License", 1, true)
        attendee("John")
        attendee("Mark", "mark@example.com")
        attendee("Ann", "ann@example.com")
        attendee("Paul", "paul@example.com")
        attendee("Raul", "raul@example.com")
        attendee("Roger")

        when:
        DrawResult result = drawService.drawWinnerCandidate(giveAway)

        then:
        isNotBlank(result.member.email)

        where:
        i << (1..RANDOM_TESTS_RUN_LIMIT)
    }

    def "should not allow attendee win more than one prize"() {
        setup:
        GiveAway ebook = giveAwayWithPrizeAndAmount("Spring Boot in Action ebook", 1)
        GiveAway license = giveAwayWithPrizeAndAmount("jProfiler license key", 1)

        markAsWinner(attendee("Joe Doe"), ebook)
        attendee("Paul")
        attendee("Roman")
        attendee("Michael")
        attendee("George")

        when:
        DrawResult result = drawService.drawWinnerCandidate(license)

        then:
        result.member.name != "Joe Doe"

        where:
        i << (1..RANDOM_TESTS_RUN_LIMIT)
    }

    def "should not allow attendee to draw the prize if he won it in the past"() {
        setup:
        GiveAway previousGiveaway = giveAwayWithPrizeAndAmountAndEvent("IntelliJ IDEA license", 1, event("Some previous event", DateTime.parse("2015-02-03T20:00:00")))
        GiveAway currentGiveaway = giveAwayWithPrizeAndAmount("IntelliJ IDEA license", 1)

        markAsWinner(attendee("Paul"), previousGiveaway)
        attendee("Roger")
        attendee("Ann")
        attendee("Lisa")
        attendee("Joel")
        attendee("Todd")
        attendee("Luke")

        when:
        DrawResult result = drawService.drawWinnerCandidate(currentGiveaway)

        then:
        result.member.name != "Paul"

        where:
        i << (1..RANDOM_TESTS_RUN_LIMIT)
    }

    def "should not allow absent attendee to draw a prize"() {
        setup:
        GiveAway giveAway = giveAwayWithPrizeAndAmount("jProfiler license key", 1)
        absent(attendee("Paul"))
        attendee("Roger")
        attendee("Lisa")
        attendee("Patrick")
        attendee("John")

        when:
        DrawResult result = drawService.drawWinnerCandidate(giveAway)

        then:
        result.member.name != "Paul"

        where:
        i << (1..RANDOM_TESTS_RUN_LIMIT)
    }

    def "should not allow attendee that won a giveaway to be selected in next draw"() {
        setup:
        GiveAway giveAway = giveAwayWithPrizeAndAmount("Something", 2)
        attendee("Paul")
        attendee("Roger")
        attendee("Lisa")
        attendee("Patrick")
        attendee("John")

        when:
        Member winner = drawService.drawWinnerCandidate(giveAway).with { drawService.confirmWinner(it.giveAway, it.member.id) }.member
        DrawResult result = drawService.drawWinnerCandidate(giveAway)

        then:
        result.member != winner

        where:
        i << (1..RANDOM_TESTS_RUN_LIMIT)
    }

    def "should not allow attendee that won a giveaway to win another prize during the same event"() {
        setup:
        GiveAway firstGiveAway = giveAwayWithPrizeAndAmount("Something", 1)
        GiveAway secondGiveAway = giveAwayWithPrizeAndAmount("Another thing", 1)
        attendee("Paul")
        attendee("Roger")
        attendee("Lisa")
        attendee("Patrick")
        attendee("John")

        when:
        Member winner = drawService.drawWinnerCandidate(firstGiveAway).with { drawService.confirmWinner(it.giveAway, it.member.id) }.member
        DrawResult result = drawService.drawWinnerCandidate(secondGiveAway)

        then:
        result.member != winner

        where:
        i << (1..RANDOM_TESTS_RUN_LIMIT)
    }

    def "should not allow attendee who skipped a giveaway to win same prize again during the same event"() {
        setup:
        GiveAway giveAway = giveAwayWithPrizeAndAmount("Something", 1)
        attendee("Paul").with {
            drawService.setGiveAwaySkippedForMember(it.member, giveAway)
        }
        attendee("Roger")

        when:
        Member winner = drawService.drawWinnerCandidate(giveAway).member

        then:
        winner.name == "Roger"
    }

    def "should draw all giveaways and confirm winners"() {
        setup:
        GiveAway firstGiveAway = giveAwayWithPrizeAndAmount("Spring Boot in Action ebook", 2)
        GiveAway secondGiveAway = giveAwayWithPrizeAndAmount("IntelliJ IDEA", 2)

        List<DrawResult> results = []
        List<String> attendees = ["Paul", "George", "Michael", "Ann", "Jessica", "Joe", "John"]
        attendees.each { attendee(it) }

        when: "Draw and confirm first winner"
        results << drawService.drawWinnerCandidate(firstGiveAway).with { drawService.confirmWinner(it.giveAway, it.member.id) }

        then: "3 prizes left"
        giveAwayController.prizesQueue(event).size() == 3

        when: "Draw and confirm second winner"
        results << drawService.drawWinnerCandidate(firstGiveAway).with { drawService.confirmWinner(it.giveAway, it.member.id) }

        then: "2 prizes left"
        giveAwayController.prizesQueue(event).size() == 2

        when: "Draw and confirm third winner"
        results << drawService.drawWinnerCandidate(secondGiveAway).with { drawService.confirmWinner(it.giveAway, it.member.id) }

        then: "1 prize left"
        giveAwayController.prizesQueue(event).size() == 1

        when: "Draw and confirm last winner"
        results << drawService.drawWinnerCandidate(secondGiveAway).with { drawService.confirmWinner(it.giveAway, it.member.id) }

        then: "No prizes left"
        giveAwayController.prizesQueue(event).isEmpty()

        and: "There are four winners"
        results.member.name.toSet().size() == 4

        and: "All winners come from attendees list"
        attendees.containsAll(results.member.name)

        where:
        i << (1..RANDOM_TESTS_RUN_LIMIT)
    }

    def "should mark attendee as absent"() {
        given:
            Attendee attendee = attendee("Roger")
        when:
            drawService.markMemberAsAbsentForCurrentDraw(attendee.member, event)
        then:
            storedAttendee(attendee).absent
    }

    private Attendee storedAttendee(Attendee attendee) {
        return attendeeRepository.findAllByMemberId(attendee.member.id)[0]
    }

    private GiveAway giveAwayWithAmount(int amount) {
        return giveAwayWithPrizeAndAmount("Test", amount)
    }

    private GiveAway giveAwayWithPrizeAndAmount(String prize, int amount, boolean emailRequired = false) {
        return giveAwayWithPrizeAndAmountAndEvent(prize, amount, event, emailRequired)
    }

    private GiveAway giveAwayWithPrizeAndAmountAndEvent(String prize, int amount, Event event, boolean emailRequired = false) {
        return giveAwayRepository.save(new GiveAway(prizeRepository.findByName(prize) ?: prizeRepository.save(new Prize(prize, "", false)), event, amount, emailRequired))
    }

    private Attendee attendee(String name, String email = "") {
        return attendeeRepository.save(new Attendee(event, memberRepository.save(new Member(counter.getAndIncrement(), name, "", email))))
    }

    private DrawResult markAsWinner(Attendee attendee, GiveAway giveAway) {
        return drawResultRepository.save(new DrawResult(giveAway, attendee.member))
    }

    private Event event(String name, DateTime dateTime) {
        return eventRepository.save(new Event(counter.getAndIncrement(), name, dateTime))
    }

    private Attendee absent(Attendee attendee) {
        drawService.markMemberAsAbsentForCurrentDraw(attendee.member, event)
        return attendee
    }
}
