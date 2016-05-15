package pl.jug.torun.xenia.service

import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.dao.MeetupMemberRepository
import pl.jug.torun.xenia.meetup.MeetupClient
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.Member
import pl.jug.torun.xenia.model.meetup.MeetupMember
import spock.lang.Specification

/**
 * Unit tests for MeetupEventsService
 *
 * @author Marcin Świerczyński
 */
class MeetupEventsServiceSpec extends Specification {

    private MeetupEventsService service

    EventRepository eventRepository = Mock(EventRepository)
    MeetupMemberRepository meetupMemberRepository = Mock(MeetupMemberRepository)
    MeetupClient meetupClient = Mock(MeetupClient)

    void setup() {
        service = new MeetupEventsService()
        service.eventRepository = eventRepository
        service.meetupMemberRepository = meetupMemberRepository
        service.meetupClient = meetupClient
    }

    void "should update member details based on remote data"() {
        given:
        Member remoteMember = new Member(id: 10, displayName: 'Marcin')
        Member localMember = new Member(id: 10, displayName: 'Marci')

        Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event', attendees: [remoteMember])
        meetupClient.findAllEvents() >> [remoteEvent]

        Event localEvent = new Event(id: 2, meetupId: 2, title: 'Event', attendees: [localMember])
        eventRepository.findAll() >> [localEvent]

        meetupClient.findAllAttendeesOfEvent(2) >> [new MeetupMember(id: 100, member: remoteMember)]
        meetupMemberRepository.findOne(100) >> new MeetupMember(id: 100, member: localMember)

        when:
        service.refreshEvents()

        then:
        1 * meetupMemberRepository.save(new MeetupMember(id: 100, member: remoteMember))
        localEvent.attendees.first().displayName == 'Marcin'
    }

}
