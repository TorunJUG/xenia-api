package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.rest.dto.AttendeeResponse
import pl.jug.torun.xenia.rest.dto.AttendeesResponse

/**
 * Created by mephi_000 on 06.09.14.
 */
@RestController
@RequestMapping("/event/{eventId}")
class AttendeesController {
    
    @Autowired
    EventRepository eventRepository
    
    @RequestMapping(value = "/attendees", method = RequestMethod.GET, produces = ["application/json"])
    AttendeesResponse getAttendees(@PathVariable("eventId") long eventId) {
        return new AttendeesResponse(attendees: eventRepository.getOne(eventId).attendees.collect {new AttendeeResponse(it)})
    }

    @RequestMapping(value = '/attendee/{id}', method = RequestMethod.GET, produces = ["application/json"])
    AttendeeResponse getAttendee(@PathVariable("eventId") long eventId, @PathVariable("id") long id) {
        def event = eventRepository.getOne(eventId)
        return new AttendeeResponse(event.attendees.find{it.id == id})
    }
     
}
