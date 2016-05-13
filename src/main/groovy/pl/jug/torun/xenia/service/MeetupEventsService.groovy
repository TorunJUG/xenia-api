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
        List<Event> events = meetupClient.findAllEvents()
        List<Event> existingEvents = this.findAll()

        events.each { remoteEvent ->
            Event event = existingEvents.find { it.meetupId == remoteEvent.meetupId }
            if (event) {
                updateEvent(event, remoteEvent)
            } else {
                createNewEvent(remoteEvent)
            }
        }
    }

    private Event updateEvent(Event existingEvent, Event remoteEvent) {
        existingEvent.title = remoteEvent.title

        List<Member> members = persistentGetAttendees(remoteEvent)
        List<Long> existingMembersIds = existingEvent.attendees.inject([]) { acc, member -> acc << member.id }

        members.findAll { !existingMembersIds.contains(it.id) }.each {
            existingEvent.attendees.add(it)
        }

        eventRepository.save(existingEvent)
        return existingEvent
    }

    private Event createNewEvent(Event remoteEvent) {
        remoteEvent.attendees = persistentGetAttendees(remoteEvent)
        eventRepository.save(remoteEvent)
        return remoteEvent
    }

    private List<Member> persistentGetAttendees(Event remoteEvent) {
        List<MeetupMember> members = meetupClient.findAllAttendeesOfEvent(remoteEvent.meetupId)
        return members.collect {
            def meetupMember = meetupMemberRepository.findOne(it.id)
            if (meetupMember) {
                meetupMember.member.displayName = it.member.displayName
                meetupMember.member.photoUrl = it.member.photoUrl
                meetupMemberRepository.save(meetupMember)
                meetupMember
            } else meetupMemberRepository.save(it)
        }.member
    }

}
