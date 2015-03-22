package pl.jug.torun.xenia.rest.dto

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
    List<DrawnMember> winners = []
    List<DrawnMember> drawn = []
    String imageUrl

    GiveAwayResponse(GiveAway giveAway) {
        this.enabled = giveAway.amount > giveAway.draws.findAll { it.confirmed }.size()
        this.id = giveAway.id
        this.prizeId = giveAway.prize.id
        this.amount = giveAway.amount
        this.prizeName = giveAway.prize.name
        this.imageUrl = giveAway.prize.imageUrl
        this.drawn = giveAway.draws.collect {
            new DrawnMember(it.attendee, it.id, it.confirmed)
        }

        this.winners = giveAway.draws?.inject([]) { winners, draw ->
            draw?.confirmed ? winners << new DrawnMember(draw?.attendee, draw?.id, true) : winners
        }
    }

    private static class DrawnMember {
        final long id
        final long drawId
        final String displayName
        final boolean winner
        final String photoUrl

        DrawnMember(Member member, long drawId, boolean winner) {
            this.id = member?.id
            this.drawId = drawId
            this.displayName = member?.displayName
            this.photoUrl = member?.photoUrl
            this.winner = winner
        }
    }
}
