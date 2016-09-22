package pl.jug.torun.xenia.meetup

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByName(final String name)
}
