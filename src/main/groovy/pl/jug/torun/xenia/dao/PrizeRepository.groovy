package pl.jug.torun.xenia.dao

import org.springframework.data.jpa.repository.JpaRepository
import pl.jug.torun.xenia.model.Prize

interface PrizeRepository extends JpaRepository<Prize, Long> {
    long countByName(String name)
    long countByNameAndIdNot(String name, long id)
    List<Prize> findAllByDeleted(boolean deleted)
    List<Prize> findAllByNameStartingWithIgnoreCase(String s)
    List<Prize> findAllByProducerStartingWithIgnoreCase(String s)
    List<Prize> findAllBySponsorNameStartingWithIgnoreCase(String s)
}
