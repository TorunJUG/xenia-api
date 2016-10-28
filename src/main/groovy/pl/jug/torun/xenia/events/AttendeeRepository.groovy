package pl.jug.torun.xenia.events

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AttendeeRepository extends JpaRepository<Attendee, Attendee.Id> {

    @Query("SELECT a FROM Attendee a WHERE a.id.event.id = :eventId")
    List<Attendee> findAllByEventId(@Param("eventId") long eventId)

    @Query("SELECT a FROM Attendee a WHERE a.id.member.id = :memberId")
    List<Attendee> findAllByMemberId(@Param("memberId") long memberId)

    @Query("SELECT a FROM Attendee a WHERE a.id.event.id = :eventId AND a.absent = false")
    List<Attendee> findAllPresentMembersAtTheEvent(@Param("eventId") long eventId)
}
