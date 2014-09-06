package pl.jug.torun.xenia.rest

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.rest.dto.GiveAwayRequest

/**
 * Created by mephi_000 on 06.09.14.
 */
public interface GiveAwayServiceInterface {
    
    GiveAway saveGiveAway(long eventId, GiveAwayRequest request)

}