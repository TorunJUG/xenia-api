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

        remoteEvents.each { remoteEvent ->
            Event event = localEvents.find { it.meetupId == remoteEvent.meetupId }
            if (event) {
                updateEvent(event, remoteEvent)
            } else {
                createNewEvent(remoteEvent)
            }
        }
    }

    private Event updateEvent(Event existingEvent, Event remoteEvent) {
        existingEvent.title = remoteEvent.title
        existingEvent = refreshMeetupAttendees(remoteEvent, existingEvent)
        eventRepository.save(existingEvent)
        return existingEvent
    }

    private Event createNewEvent(Event remoteEvent) {
        List<MeetupMember> remoteMembers = meetupClient.findAllAttendeesOfEvent(remoteEvent.meetupId)
        remoteEvent.attendees = createOrUpdateAttendees(remoteMembers)
        eventRepository.save(remoteEvent)
        return remoteEvent
    }

    private Event refreshMeetupAttendees(Event remoteEvent, Event existingEvent) {
        List<MeetupMember> remoteMembers = meetupClient.findAllAttendeesOfEvent(remoteEvent.meetupId)

        List<Member> members = createOrUpdateAttendees(remoteMembers)
        List<Long> existingMembersIds = existingEvent.attendees*.id
        members.findAll { !existingMembersIds.contains(it.id) }.each {
            existingEvent.attendees.add(it)
        }

        removeAttendees(remoteMembers, existingEvent)

        return existingEvent
    }

    private List<Member> createOrUpdateAttendees(List<MeetupMember> remoteMembers) {
        return remoteMembers.collect { MeetupMember remoteMeetupMember ->
            MeetupMember localMeetupMember = meetupMemberRepository.findOne(remoteMeetupMember.id)
            if (localMeetupMember) {
                localMeetupMember.member.displayName = remoteMeetupMember.member.displayName
                localMeetupMember.member.photoUrl = remoteMeetupMember.member.photoUrl
                meetupMemberRepository.save(localMeetupMember)
                return localMeetupMember
            } else {
                return meetupMemberRepository.save(remoteMeetupMember)
            }
        }.member
    }

    private void removeAttendees(List<MeetupMember> remoteMembers, Event existingEvent) {
        List<Member> attendees = new ArrayList<>(existingEvent.attendees)
        attendees.collect { Member localMember ->
            MeetupMember meetupMember = meetupMemberRepository.getByMember(localMember)
            if (!remoteMembers*.id.contains(meetupMember.id)) {
                existingEvent.attendees.remove(localMember)
            }
        }
    }

}
