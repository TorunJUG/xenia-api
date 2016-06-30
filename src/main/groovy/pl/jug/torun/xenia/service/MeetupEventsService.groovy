package pl.jug.torun.xenia.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.dao.MeetupMemberRepository
import pl.jug.torun.xenia.meetup.MeetupClient
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.Member
import pl.jug.torun.xenia.model.meetup.MeetupMember

/**
 * Interacts with events hosted on Meetup
 *
 * @author Marcin Świerczyński
 */
@Service
class MeetupEventsService implements EventsService {

    @Autowired
    EventRepository eventRepository

    @Autowired
    MeetupMemberRepository meetupMemberRepository

    @Autowired
    MeetupClient meetupClient

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll()
    }

    @Override
    public void refreshEvents() {
        List<Event> remoteEvents = meetupClient.findAllEvents()
        List<Event> localEvents = this.findAll()
        createOrUpdateLocalEvents(remoteEvents, localEvents)
    }

    private List<Event> createOrUpdateLocalEvents(List<Event> remoteEvents, List<Event> localEvents) {
        remoteEvents.each { Event remoteEvent ->
            Event localEvent = localEvents.find { it.meetupId == remoteEvent.meetupId }
            if (localEvent) {
                refreshEvent(localEvent)
            } else {
                refreshEvent(remoteEvent)
            }
        }
    }

    private Event refreshEvent(Event event) {
        List<MeetupMember> remoteMembers = meetupClient.findAllAttendeesOfEvent(event.meetupId)
        createOrUpdateAttendees(remoteMembers, event)
        removeAttendees(remoteMembers, event)
        return eventRepository.save(event)
    }

    private void createOrUpdateAttendees(List<MeetupMember> remoteMembers, Event event) {
        List<Member> members = persistMembersLocally(remoteMembers)
        addMissingAttendeesToEvent(event, members)
    }

    private List<Member> persistMembersLocally(List<MeetupMember> remoteMembers) {
        return remoteMembers.collect { MeetupMember remoteMeetupMember ->
            MeetupMember localMeetupMember = meetupMemberRepository.findOne(remoteMeetupMember.id)
            if (localMeetupMember) {
                localMeetupMember.member.displayName = remoteMeetupMember.member.displayName
                localMeetupMember.member.photoUrl = remoteMeetupMember.member.photoUrl
                return meetupMemberRepository.save(localMeetupMember)
            } else {
                return meetupMemberRepository.save(remoteMeetupMember)
            }
        }.member
    }

    private static void addMissingAttendeesToEvent(Event event, List<Member> members) {
        List<Long> localMembersIds = event.attendees*.id
        members.findAll { !localMembersIds.contains(it.id) }.each {
            event.attendees.add(it)
        }
    }

    private void removeAttendees(List<MeetupMember> remoteMembers, Event event) {
        List<Member> attendees = new ArrayList<>(event.attendees)
        attendees.each { Member localMember ->
            MeetupMember meetupMember = meetupMemberRepository.getByMember(localMember)
            boolean memberNoLongerAttendsEvent = !remoteMembers*.id.contains(meetupMember.id)
            if (memberNoLongerAttendsEvent) {
                event.attendees.remove(localMember)
            }
        }
    }
}
