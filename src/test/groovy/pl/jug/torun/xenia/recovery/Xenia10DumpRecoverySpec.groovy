package pl.jug.torun.xenia.recovery

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.draw.DrawResultRepository
import pl.jug.torun.xenia.draw.GiveAwayRepository
import pl.jug.torun.xenia.events.AttendeeRepository
import pl.jug.torun.xenia.events.EventRepository
import pl.jug.torun.xenia.meetup.MeetupClient
import pl.jug.torun.xenia.meetup.MeetupRestTemplate
import pl.jug.torun.xenia.meetup.MemberRepository
import pl.jug.torun.xenia.prizes.PrizeRepository
import spock.lang.Specification
import spock.lang.Subject

import java.nio.charset.Charset

@DataJpaTest
@ContextConfiguration
class Xenia10DumpRecoverySpec extends Specification {

    private static final String MEETUP_KEY = "24023e9f-364f-4929-bca0-a565fb9a1866"
    private static final String MEETUP_GROUP_URL = "Meetup-Group-URL"

    @Subject
    private Xenia10DumpRecovery dumpRecovery

    private ClientHttpRequestFactory requestFactory = new MockClientHttpRequestFactory()
    private MeetupRestTemplate restTemplate = new MeetupRestTemplate(MEETUP_KEY, MEETUP_GROUP_URL, requestFactory)
    private MeetupClient meetupClient = new MeetupClient(restTemplate)

    @Autowired
    private EventRepository eventRepository
    @Autowired
    private MemberRepository memberRepository
    @Autowired
    private AttendeeRepository attendeeRepository
    @Autowired
    private PrizeRepository prizeRepository
    @Autowired
    private GiveAwayRepository giveAwayRepository
    @Autowired
    private DrawResultRepository drawResultRepository
    @Autowired
    private ObjectMapper objectMapper

    def setup() {
        dumpRecovery = new Xenia10DumpRecovery(
                meetupClient,
                eventRepository,
                memberRepository,
                attendeeRepository,
                prizeRepository,
                giveAwayRepository,
                drawResultRepository,
                objectMapper
        )
    }

    def "should import events"() {
        when:
        dumpRecovery.recover()

        then:
        eventRepository.findAll().name == ["Meetup #1", "Meetup #2"]
    }

    def "should import prizes"() {
        when:
        dumpRecovery.recover()

        then:
        prizeRepository.findAll().name == ["IntelliJ IDEA Licence Key", "Structure101 Licence Key", "'Scala in Action' eBook"]
    }

    def "should import members"() {
        when:
        dumpRecovery.recover()

        then:
        memberRepository.findAll().name == ["Joe Doe", "Paul Smith", "Margaret Lee Cooper"]
    }

    def "should import giveaways"() {
        when:
        dumpRecovery.recover()

        then:
        giveAwayRepository.findAllByEvent(eventRepository.findOne(100023L)).collect {
            [name: it.prize.name, amount: it.amount]
        } == [[name: "IntelliJ IDEA Licence Key", amount: 1], [name: "Structure101 Licence Key", amount: 1]]

        and:
        giveAwayRepository.findAllByEvent(eventRepository.findOne(100024L)).collect {
            [name: it.prize.name, amount: it.amount]
        } == [[name: "'Scala in Action' eBook", amount: 1], [name: "IntelliJ IDEA Licence Key", amount: 1]]
    }

    def "should import draw results"() {
        when:
        dumpRecovery.recover()

        then:
        drawResultRepository.findAllByEvent(eventRepository.findOne(100023L)).collect {
            [member: it.member.name, prize: it.giveAway.prize.name]
        } == [[member: "Joe Doe", prize: "IntelliJ IDEA Licence Key"], [member: "Paul Smith", prize: "Structure101 Licence Key"]]

        and:
        drawResultRepository.findAllByEvent(eventRepository.findOne(100024L)).collect {
            [member: it.member.name, prize: it.giveAway.prize.name]
        } == [[member: "Paul Smith", prize: "'Scala in Action' eBook"], [member: "Margaret Lee Cooper", prize: "IntelliJ IDEA Licence Key"]]
    }

    def "should import attendees"() {
        when:
        dumpRecovery.recover()

        then:
        attendeeRepository.findAllByEventId(100023L).collect {
            [name: it.member.name]
        } == [[name: "Joe Doe"], [name: "Paul Smith"]]

        and:
        attendeeRepository.findAllByEventId(100024L).collect {
            [name: it.member.name]
        } == [[name: "Paul Smith"], [name: "Margaret Lee Cooper"]]
    }

    def "should import 2 events"() {
        when:
        dumpRecovery.recover()

        then:
        eventRepository.count() == 2
    }

    def "should import 3 members"() {
        when:
        dumpRecovery.recover()

        then:
        memberRepository.count() == 3
    }

    def "should import 4 attendees"() {
        when:
        dumpRecovery.recover()

        then:
        attendeeRepository.count() == 4
    }

    def "should import 3 prizes"() {
        when:
        dumpRecovery.recover()

        then:
        prizeRepository.count() == 3
    }

    def "should import 4 giveaways"() {
        when:
        dumpRecovery.recover()

        then:
        giveAwayRepository.count() == 4
    }

    def "should import 4 draw results"() {
        when:
        dumpRecovery.recover()

        then:
        drawResultRepository.count() == 4
    }

    private static class MockClientHttpRequestFactory implements ClientHttpRequestFactory {

        private final ObjectMapper objectMapper = new ObjectMapper()
        private final Charset utf8 = Charset.forName("UTF-8")

        @Override
        ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
            switch (uri.toString()) {
                case 'https://api.meetup.com/2/events?only=id,name,time&status=upcoming,past&key=24023e9f-364f-4929-bca0-a565fb9a1866&group_urlname=Meetup-Group-URL':
                    Map body = [results: [
                            [id: 100023, name: "Meetup #1", time: 1395853200000],
                            [id: 100024, name: "Meetup #2", time: 1398268800000]
                    ]]
                    return mockRequestWith(uri, httpMethod, body)

                case 'https://api.meetup.com/2/rsvps?event_id=100023&only=member,member_photo,answers&rsvp=yes&key=24023e9f-364f-4929-bca0-a565fb9a1866&group_urlname=Meetup-Group-URL':
                    Map body = [results: [
                            [
                                    answers: [
                                            "730e3c1b-aa68-4f14-b919-52af65c4d5a8@mailinator.com"
                                    ],
                                    member : [
                                            member_id: 100,
                                            name     : "Joe Doe"
                                    ]
                            ],
                            [
                                    answers: [
                                            "65e2d937-6b2e-4ca6-bf01-cc79b5b38f4c@mailinator.com"
                                    ],
                                    member : [
                                            member_id: 101,
                                            name     : "Paul Smith"
                                    ]
                            ]
                    ]]
                    return mockRequestWith(uri, httpMethod, body)

                case 'https://api.meetup.com/2/rsvps?event_id=100024&only=member,member_photo,answers&rsvp=yes&key=24023e9f-364f-4929-bca0-a565fb9a1866&group_urlname=Meetup-Group-URL':
                    Map body = [results: [
                            [
                                    answers: [
                                            "65e2d937-6b2e-4ca6-bf01-cc79b5b38f4c@mailinator.com"
                                    ],
                                    member : [
                                            member_id: 101,
                                            name     : "Paul Smith"
                                    ]
                            ],
                            [
                                    answers: [],
                                    member : [
                                            member_id: 102,
                                            name     : "Margaret Lee Cooper"
                                    ]
                            ]
                    ]]
                    return mockRequestWith(uri, httpMethod, body)

            }

            return null
        }

        private MockClientHttpRequest mockRequestWith(URI uri, HttpMethod httpMethod, Map body) {
            String json = objectMapper.writeValueAsString(body)

            MockClientHttpResponse response = new MockClientHttpResponse(json.getBytes(utf8), HttpStatus.OK)
            response.getHeaders().add("Content-Type", "application/json")

            MockClientHttpRequest request = new MockClientHttpRequest(httpMethod, uri)
            request.setResponse(response)
            return request
        }
    }
}
