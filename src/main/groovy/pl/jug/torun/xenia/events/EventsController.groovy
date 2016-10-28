package pl.jug.torun.xenia.events

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = "/events", produces = "application/json")
final class EventsController {

    private final EventRepository eventRepository
    private final EventsSynchronizationService synchronizationService

    @Autowired
    EventsController(EventRepository eventRepository, EventsSynchronizationService synchronizationService) {
        this.eventRepository = eventRepository
        this.synchronizationService = synchronizationService
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Event> listAll() {
        return eventRepository.findAll()
                .sort { it.startDateTime }
                .reverse()
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public List<Event> refresh() {
        synchronizationService.synchronizeLocalEventsWithRemoteService()
        return listAll()
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event eventDetails(@PathVariable("id") long id) {
        return eventRepository.findOne(id)
    }
}
