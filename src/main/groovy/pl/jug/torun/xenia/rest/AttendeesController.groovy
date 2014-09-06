package pl.jug.torun.xenia.rest

import org.springframework.stereotype.Controller
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
@RequestMapping("/event/{eventId}/attendees")
class AttendeesController {
    
    
    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    AttendeesResponse getAttendees() {
        return new AttendeesResponse(attendees: [
                new AttendeeResponse(id: 1, displayName: 'Szymon', photoUrl: 'http://photos3.meetupstatic.com/photos/member/2/a/3/2/thumb_179350802.jpeg'),
                new AttendeeResponse(id: 2, displayName: 'Zbyszko', photoUrl: 'http://torun.jug.pl/images/speakers/papierski-zbyszko.jpg')
        ])
    }
     
}
