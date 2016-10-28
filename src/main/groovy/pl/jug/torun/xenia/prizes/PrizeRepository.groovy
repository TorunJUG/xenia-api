package pl.jug.torun.xenia.prizes

import org.springframework.data.jpa.repository.JpaRepository

interface PrizeRepository extends JpaRepository<Prize, Long> {
    Prize findByName(final String name)
    List<Prize> findAllByInactiveOrderByIdAsc(boolean inactive)
}