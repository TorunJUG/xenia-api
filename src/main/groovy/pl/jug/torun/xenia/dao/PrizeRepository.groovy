package pl.jug.torun.xenia.dao

import org.springframework.data.jpa.repository.JpaRepository
import pl.jug.torun.xenia.model.Prize

interface PrizeRepository extends JpaRepository<Prize, Long> {
}
