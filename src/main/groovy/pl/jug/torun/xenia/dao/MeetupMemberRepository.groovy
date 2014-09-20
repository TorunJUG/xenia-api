package pl.jug.torun.xenia.dao

import org.springframework.data.jpa.repository.JpaRepository
import pl.jug.torun.xenia.model.meetup.MeetupMember

/**
 * Created by mephi_000 on 2014-09-17.
 */
public interface MeetupMemberRepository extends JpaRepository<MeetupMember, Long> {

}