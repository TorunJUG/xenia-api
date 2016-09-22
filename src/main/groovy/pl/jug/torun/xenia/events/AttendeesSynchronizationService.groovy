package pl.jug.torun.xenia.events

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import pl.jug.torun.xenia.meetup.MeetupClient
import pl.jug.torun.xenia.meetup.MemberRepository

@Service
class AttendeesSynchronizationService {

    private final AttendeeRepository attendeeRepository
    private final MemberRepository memberRepository
    private final MeetupClient meetupClient

    @Autowired
    AttendeesSynchronizationService(AttendeeRepository attendeeRepository, MemberRepository memberRepository, MeetupClient meetupClient) {
        this.attendeeRepository = attendeeRepository
        this.memberRepository = memberRepository
        this.meetupClient = meetupClient
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void synchronizeLocalAttendeesWithRemoteService(final Event event) {
        attendeeRepository.findAllByEventId(event.id).each {
            attendeeRepository.delete(it)
        }

        meetupClient.getAllEventAttendees(event.id).each {
            memberRepository.save(it)
            attendeeRepository.save(new Attendee(event, it))
        }
    }
}
