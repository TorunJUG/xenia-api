package pl.jug.torun.xenia.rest.dto

import groovy.transform.Immutable
import pl.jug.torun.xenia.model.Prize

/**
 * Created by mephi_000 on 06.09.14.
 */

class PrizeRequest {
    String name
    String producer
    String sponsorName
    String imageUrl

    PrizeRequest(String name, String producer, String sponsorName, String imageUrl) {
        this.name = name
        this.producer = producer
        this.sponsorName = sponsorName
        this.imageUrl = imageUrl
    }

    Prize toPrize() {
        return new Prize(name: name, producer: producer, sponsorName: sponsorName, imageUrl: imageUrl)
    }
}
