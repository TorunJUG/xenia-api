package pl.jug.torun.xenia.meetup

import org.joda.time.DateTime
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.http.ClassPathResourceClientHttpRequestFactory
import spock.lang.Specification

class MeetupClientSpec extends Specification implements MeetupClientAware {

    static final Long MEETUP_EVENT_ID = 1L

    def "should parse JSON response to the list of event objects"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/01_events.json', 'application/json'))

        when:
        List<Event> events = meetupClient.getAllEvents()

        then:
        events.size() == 38
    }

    def "should get the first event from events list"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/01_events.json', 'application/json'))

        when:
        List<Event> events = meetupClient.getAllEvents()

        then:
        events.first().name == '1. spotkanie Toruń JUG'

        and:
        events.first().id == 173040582

        and:
        events.first().startDateTime == DateTime.parse('2014-03-26T18:00:00.000+0100')
    }

    def "should get the last event from events list"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/01_events.json', 'application/json'))

        when:
        List<Event> events = meetupClient.getAllEvents()

        then:
        events.last().name == 'JDD 2016 - Ticket Raffle'

        and:
        events.last().id == 234115403

        and:
        events.last().startDateTime == DateTime.parse('2016-09-28T19:30:00.000+0200')
    }

    def "should load the list of event attendees"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees.json', 'application/json'))

        when:
        List<Member> attendees = meetupClient.getAllEventAttendees(MEETUP_EVENT_ID)

        then:
        attendees.size() == 5
    }

    def "should load 1st attendee"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees.json', 'application/json'))

        when:
        Member attendee = meetupClient.getAllEventAttendees(MEETUP_EVENT_ID).first()

        then:
        attendee.id == 324

        and:
        attendee.name == "Joe Doe"

        and:
        attendee.email == "730e3c1b-aa68-4f14-b919-52af65c4d5a8@mailinator.com"

        and:
        attendee.photoUrl == "https://placeholdit.imgix.net/~text?txtsize=33&txt=Avatar&w=350&h=350"
    }

    def "should load 2nd attendee"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees.json', 'application/json'))

        when:
        Member attendee = meetupClient.getAllEventAttendees(MEETUP_EVENT_ID).get(1)

        then:
        attendee.id == 325

        and:
        attendee.name == "Paul Smith"

        and:
        attendee.email == "65e2d937-6b2e-4ca6-bf01-cc79b5b38f4c@mailinator.com"

        and:
        attendee.photoUrl == ""
    }

    def "should load 3rd attendee"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees.json', 'application/json'))

        when:
        Member attendee = meetupClient.getAllEventAttendees(MEETUP_EVENT_ID).get(2)

        then:
        attendee.id == 326

        and:
        attendee.name == "Mark Spencer"

        and:
        attendee.email == ""

        and:
        attendee.photoUrl == "https://placeholdit.imgix.net/~text?txtsize=33&txt=Avatar&w=350&h=350"
    }

    def "should load 4th attendee"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees.json', 'application/json'))

        when:
        Member attendee = meetupClient.getAllEventAttendees(MEETUP_EVENT_ID).get(3)

        then:
        attendee.id == 327

        and:
        attendee.name == "Peter Johnson"

        and:
        attendee.email == "5ba21436-b1ff-4979-a593-949e2c658a61@mailinator.com"

        and:
        attendee.photoUrl == ""
    }

    def "should load 5th attendee"() {
        setup:
        restTemplate.setRequestFactory(new ClassPathResourceClientHttpRequestFactory('/json/02_attendees.json', 'application/json'))

        when:
        Member attendee = meetupClient.getAllEventAttendees(MEETUP_EVENT_ID).last()

        then:
        attendee.id == 328

        and:
        attendee.name == "Michael Bacon"

        and:
        attendee.email == "16239485-0d61-4e0a-9556-ff516a000da5@mailinator.com"

        and:
        attendee.photoUrl == "https://placeholdit.imgix.net/~text?txtsize=33&txt=Avatar&w=350&h=350"
    }
}