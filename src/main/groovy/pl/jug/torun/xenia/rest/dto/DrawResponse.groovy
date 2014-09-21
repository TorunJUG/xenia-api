package pl.jug.torun.xenia.rest.dto

import pl.jug.torun.xenia.model.Draw

/**
 * Created by mephi_000 on 06.09.14.
 */
class DrawResponse {
    long id
    long attendeeId
    String drawDate
    boolean confirmed
    String attendeeName

    DrawResponse(Draw draw) {
        this.attendeeId = draw.attendee.id
        this.attendeeName = draw.attendee.displayName
        this.id = draw.id
        this.drawDate = draw.drawDate
        this.confirmed = draw.confirmed
    }
}
