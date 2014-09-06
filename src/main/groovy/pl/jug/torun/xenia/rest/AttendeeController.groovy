package pl.jug.torun.xenia.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.rest.dto.AttendeeResponse
import pl.jug.torun.xenia.rest.dto.AttendeesResponse

/**
 * Created by mephi_000 on 06.09.14.
 */

@RestController
@RequestMapping("/attendee")
class AttendeeController {

  
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = ["application/json"])
    AttendeeResponse getAttendees(@PathVariable('id') int id) {
        new AttendeeResponse(id: id, displayName: 'Zbyszko', photoUrl: 'http://torun.jug.pl/images/speakers/papierski-zbyszko.jpg')
    }
}
    
