package pl.jug.torun.xenia.rest.dto

import pl.jug.torun.xenia.model.Member

/**
 * Created by mephi_000 on 06.09.14.
 */
class AttendeeResponse {
    long id
    String displayName
    String photoUrl
    
    
    AttendeeResponse(Member attendee) {
        this.id = attendee.id
        this.displayName = attendee.displayName
        this.photoUrl = attendee.photoUrl
    }
   
}
