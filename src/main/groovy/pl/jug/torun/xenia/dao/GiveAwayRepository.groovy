package pl.jug.torun.xenia.dao

import org.springframework.data.jpa.repository.JpaRepository
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.model.Prize

/**
 * Created by mephi_000 on 06.09.14.
 */
interface GiveAwayRepository extends JpaRepository<GiveAway, Long> {
    List<GiveAway> findByPrize(Prize prize)
    long countByPrizeId(long prizeId)
}
