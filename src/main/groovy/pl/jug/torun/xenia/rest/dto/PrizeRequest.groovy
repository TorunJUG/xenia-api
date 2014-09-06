package pl.jug.torun.xenia.rest.dto

import groovy.transform.Immutable
import pl.jug.torun.xenia.model.Prize

/**
 * Created by mephi_000 on 06.09.14.
 */
@Immutable
class PrizeRequest {
    String name
    String producer
    String sponsorName
    String imageUrl

    Prize toPrize() {
        return new Prize(name: name, producer: producer, sponsorName: sponsorName, imageUrl: imageUrl)
    }
}
