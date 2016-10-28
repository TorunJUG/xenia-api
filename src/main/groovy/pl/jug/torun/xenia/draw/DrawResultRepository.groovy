package pl.jug.torun.xenia.draw

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.prizes.Prize

interface DrawResultRepository extends JpaRepository<DrawResult, Long> {
    Long countAllByGiveAway(final GiveAway giveAway)

    @Query("SELECT dr FROM DrawResult dr WHERE dr.giveAway.event = :event")
    List<DrawResult> findAllByEvent(@Param("event") final Event event)

    @Query("SELECT dr FROM DrawResult dr WHERE dr.giveAway.prize = :prize")
    List<DrawResult> findAllByPrize(@Param("prize") final Prize prize)
}
