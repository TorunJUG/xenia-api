package pl.jug.torun.xenia.draw

import org.springframework.data.jpa.repository.JpaRepository
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.prizes.Prize

interface GiveAwayRepository extends JpaRepository<GiveAway, Long> {
    List<GiveAway> findAllByEvent(final Event event)
    GiveAway findByPrizeAndEvent(final Prize prize, final Event event)
}