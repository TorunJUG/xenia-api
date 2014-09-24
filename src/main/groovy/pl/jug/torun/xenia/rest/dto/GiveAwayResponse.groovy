package pl.jug.torun.xenia.rest.dto

import groovy.transform.Immutable
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.model.Member

/**
 * Created by mephi_000 on 06.09.14.
 */
class GiveAwayResponse {
    int id
    int prizeId
    int amount
    String prizeName
    Boolean enabled
    List<Winner> winners = []
    String imageUrl

    GiveAwayResponse(GiveAway giveAway) {
        this.enabled = giveAway.amount > giveAway.draws.findAll {it.confirmed}.size()
        this.id = giveAway.id
        this.prizeId = giveAway.prize.id
        this.amount = giveAway.amount
        this.prizeName = giveAway.prize.name
        this.imageUrl = giveAway.prize.imageUrl

        this.winners = giveAway.draws?.inject([]) { winners, draw ->
            draw?.confirmed ? winners << new Winner(draw?.attendee) : winners
        }
    }

    private static class Winner {
        final long id
        final String displayName

        Winner(Member member) {
            this.id = member?.id
            this.displayName = member?.displayName
        }
    }
}
