package pl.jug.torun.xenia.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import pl.jug.torun.xenia.rest.dto.AttendeeResponse
import pl.jug.torun.xenia.rest.dto.AttendeesResponse

/**
 * Created by mephi_000 on 06.09.14.
 */

@Controller
@RequestMapping("/attendee")
class AttendeeController {


    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    @ResponseBody AttendeeResponse getAttendees() {
        new AttendeeResponse(id: 2, displayName: 'Zbyszko', photoUrl: 'http://torun.jug.pl/images/speakers/papierski-zbyszko.jpg')
    }
}
    
