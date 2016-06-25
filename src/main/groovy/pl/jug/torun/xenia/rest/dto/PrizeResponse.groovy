package pl.jug.torun.xenia.rest.dto

import groovy.transform.ToString
import pl.jug.torun.xenia.model.Prize

/**
 * Created by mephi_000 on 06.09.14.
 */
@ToString(includePackage = false)
class PrizeResponse {
    final Long id
    final String name
    final String producer
    final String imageUrl
    final String sponsorName

    PrizeResponse(Prize prize) {
        id = prize?.id
        name = prize?.name
        producer = prize?.producer
        imageUrl = prize?.imageUrl
        sponsorName = prize?.sponsorName
    }
}
