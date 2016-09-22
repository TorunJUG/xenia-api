package pl.jug.torun.xenia.events

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jug.torun.xenia.meetup.MeetupClient

import javax.transaction.Transactional

@Service
class EventsSynchronizationService {

    private final EventRepository eventRepository
    private final MeetupClient meetupClient

    @Autowired
    EventsSynchronizationService(EventRepository eventRepository, MeetupClient meetupClient) {
        this.eventRepository = eventRepository
        this.meetupClient = meetupClient
    }

    @Transactional
    public void synchronizeLocalEventsWithRemoteService() {
        List<Event> events = meetupClient.getAllEvents()
        eventRepository.save(events)
    }
}
