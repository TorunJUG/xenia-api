package pl.jug.torun.xenia.events

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.meetup.Member

@RestController
@RequestMapping(value = "/events/{id}/attendees", produces = "application/json")
final class AttendeesController {

    private final AttendeeRepository attendeeRepository
    private final AttendeesSynchronizationService synchronizationService

    @Autowired
    AttendeesController(AttendeeRepository attendeeRepository, AttendeesSynchronizationService synchronizationService) {
        this.attendeeRepository = attendeeRepository
        this.synchronizationService = synchronizationService
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Member> listAll(@PathVariable("id") long eventId) {
        return attendeeRepository.findAllByEventId(eventId).collect { it.member }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public List<Member> refresh(@PathVariable("id") Event event) {
        synchronizationService.synchronizeLocalAttendeesWithRemoteService(event)
        return listAll(event.id)
    }
}
