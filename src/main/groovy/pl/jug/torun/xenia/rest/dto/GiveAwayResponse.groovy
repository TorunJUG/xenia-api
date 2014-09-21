package pl.jug.torun.xenia.rest.dto

import pl.jug.torun.xenia.model.GiveAway

/**
 * Created by mephi_000 on 06.09.14.
 */
class GiveAwayResponse {
    int id
    int prizeId
    int amount
    String prizeName
    
    GiveAwayResponse(GiveAway giveAway) {
        this.id = giveAway.id
        this.prizeId = giveAway.prize.id
        this.amount = giveAway.amount
        this.prizeName = giveAway.prize.name
    }
}
