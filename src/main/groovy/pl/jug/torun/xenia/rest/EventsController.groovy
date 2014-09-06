package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.meetup.MeetupClient
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.Member
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.rest.dto.EventsResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/events")
class EventsController {
    
    @Autowired
    EventRepository eventRepository

    @Autowired
    MeetupClient meetupClient

    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    EventsResponse getEvents() {
      return new EventsResponse(events: eventRepository.findAll().collect {
           event -> new EventResponse(event)
       })

    }
    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = ["application/json"])
    String refresh() {
        List<Event> events = meetupClient.findAllEvents()
        events.each { event ->
            List<Member> members = meetupClient.findAllAttendeesOfEvent(event.meetupId)
            event.attendees = members
            eventRepository.save(event)
        }

        return [status: 'OK']
    }
}
