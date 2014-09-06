package pl.jug.torun.xenia.model

import org.joda.time.LocalDateTime

/**
 * Created by mephi_000 on 06.09.14.
 */
class Event {
    String title
    LocalDateTime startDate
    LocalDateTime endDate
    Long meetupId
    List<GiveAway> giveAways
}
