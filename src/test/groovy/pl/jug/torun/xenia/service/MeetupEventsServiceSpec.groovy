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

    void "should create new event based on remote data"() {
        given:
            Member remoteMember = new Member(id: 10, displayName: 'Marcin')
            Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event 2', attendees: [])
            meetupClient.findAllEvents() >> [remoteEvent]
            eventRepository.findAll() >> []
            meetupClient.findAllAttendeesOfEvent(2) >> [new MeetupMember(id: 100, member: remoteMember)]
            meetupMemberRepository.findOne(100) >> null
            meetupMemberRepository.save(new MeetupMember(id: 100, member: remoteMember))
        when:
            service.refreshEvents()
        then:
            0 * meetupMemberRepository.getByMember(_)
            1 * eventRepository.save(remoteEvent)
    }

    void "should create new event based on remote data if different event exist"() {
        given:
            Member remoteMember = new Member(id: 10, displayName: 'Marcin')
            Member localMember = new Member(id: 11, displayName: 'Marci')
            Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event 2', attendees: [])
            meetupClient.findAllEvents() >> [remoteEvent]
            Event localEvent = new Event(id: 3, meetupId: 4, title: 'Event 3', attendees: [localMember])
            eventRepository.findAll() >> [localEvent]
            meetupClient.findAllAttendeesOfEvent(2) >> [new MeetupMember(id: 100, member: remoteMember)]
            meetupMemberRepository.findOne(100) >> null
            meetupMemberRepository.save(new MeetupMember(id: 100, member: remoteMember))
        when:
            service.refreshEvents()
        then:
            0 * meetupMemberRepository.getByMember(_)
            1 * eventRepository.save(remoteEvent)
    }

    void "should update member details based on remote data"() {
        given:
            Member remoteMember = new Member(id: 10, displayName: 'Marcin')
            Member localMember = new Member(id: 10, displayName: 'Marci')
            Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event', attendees: [])
            meetupClient.findAllEvents() >> [remoteEvent]
            Event localEvent = new Event(id: 2, meetupId: 2, title: 'Event', attendees: [localMember])
            eventRepository.findAll() >> [localEvent]
            meetupClient.findAllAttendeesOfEvent(2) >> [new MeetupMember(id: 100, member: remoteMember)]
            MeetupMember meetupMember = new MeetupMember(id: 100, member: localMember)
            meetupMemberRepository.findOne(100) >> meetupMember
            meetupMemberRepository.getByMember(localMember) >> meetupMember
        when:
            service.refreshEvents()
        then:
            1 * meetupMemberRepository.save(new MeetupMember(id: 100, member: remoteMember))
            localEvent.attendees.first().displayName == 'Marcin'
    }

    void "should create new member based on remote data"() {
        given:
            Member remoteMember = new Member(id: 10, displayName: 'Marcin')
            Member localMember = new Member(id: 11, displayName: 'Foo')
            Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event', attendees: [])
            meetupClient.findAllEvents() >> [remoteEvent]
            Event localEvent = new Event(id: 2, meetupId: 2, title: 'Event', attendees: [localMember])
            eventRepository.findAll() >> [localEvent]
            MeetupMember remoteMeetupMember = new MeetupMember(id: 110, member: remoteMember)
            meetupClient.findAllAttendeesOfEvent(2) >> [remoteMeetupMember]
            MeetupMember meetupMember = new MeetupMember(id: 100, member: localMember)
            meetupMemberRepository.findOne(100) >> null
            meetupMemberRepository.getByMember(localMember) >> meetupMember
            meetupMemberRepository.getByMember(remoteMember) >> remoteMeetupMember
        when:
            service.refreshEvents()
        then:
            1 * meetupMemberRepository.save(remoteMeetupMember) >> remoteMeetupMember
            localEvent.attendees.last().displayName == 'Marcin'
    }

    void "should remove members from local event if they are removed from remote event"() {
        given:
            Member localMember = new Member(id: 10, displayName: 'Marci')
            Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event', attendees: [])
            meetupClient.findAllEvents() >> [remoteEvent]
            Event localEvent = new Event(id: 2, meetupId: 2, title: 'Event', attendees: [localMember])
            eventRepository.findAll() >> [localEvent]
            meetupClient.findAllAttendeesOfEvent(2) >> []
            MeetupMember meetupMember = new MeetupMember(id: 100, member: localMember)
            meetupMemberRepository.findOne(100) >> meetupMember
            meetupMemberRepository.getByMember(localMember) >> meetupMember
        when:
            service.refreshEvents()
        then:
            localEvent.attendees.size() == 0
    }

    void "should remove members from local event if they are removed from remote event but other local event are still there"() {
        given:
            Member remoteMember = new Member(id: 10, displayName: 'Marcin')
            Member localMember1 = new Member(id: 10, displayName: 'Marcin')
            Member localMember2 = new Member(id: 11, displayName: 'Foo')
            Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event', attendees: [])
            meetupClient.findAllEvents() >> [remoteEvent]
            Event localEvent = new Event(id: 2, meetupId: 2, title: 'Event', attendees: [localMember1, localMember2])
            eventRepository.findAll() >> [localEvent]
            meetupClient.findAllAttendeesOfEvent(2) >> [new MeetupMember(id: 100, member: remoteMember)]
            MeetupMember meetupMember1 = new MeetupMember(id: 100, member: localMember1)
            meetupMemberRepository.findOne(100) >> meetupMember1
            meetupMemberRepository.getByMember(localMember1) >> meetupMember1
            MeetupMember meetupMember2 = new MeetupMember(id: 101, member: localMember2)
            meetupMemberRepository.findOne(101) >> meetupMember2
            meetupMemberRepository.getByMember(localMember2) >> meetupMember2
        when:
            service.refreshEvents()
        then:
            localEvent.attendees == [localMember1]
    }

    void "should not remove members from local event if they are nothing to remove"() {
        given:
            Member remoteMember = new Member(id: 10, displayName: 'Marcin')
            Member localMember = new Member(id: 10, displayName: 'Marcin')
            Event remoteEvent = new Event(id: 1, meetupId: 2, title: 'Event', attendees: [])
            meetupClient.findAllEvents() >> [remoteEvent]
            Event localEvent = new Event(id: 2, meetupId: 2, title: 'Event', attendees: [localMember])
            eventRepository.findAll() >> [localEvent]
            meetupClient.findAllAttendeesOfEvent(2) >> [new MeetupMember(id: 100, member: remoteMember)]
            MeetupMember meetupMember = new MeetupMember(id: 100, member: localMember)
            meetupMemberRepository.findOne(100) >> meetupMember
            meetupMemberRepository.getByMember(localMember) >> meetupMember
        when:
            service.refreshEvents()
        then:
            localEvent.attendees == [localMember]
    }
}
