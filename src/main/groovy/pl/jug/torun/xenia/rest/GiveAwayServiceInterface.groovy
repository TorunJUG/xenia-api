package pl.jug.torun.xenia.rest

import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.rest.dto.GiveAwayRequest

/**
 * Created by mephi_000 on 06.09.14.
 */
public interface GiveAwayServiceInterface {

    GiveAway saveGiveAway(long eventId, GiveAwayRequest request)
}
