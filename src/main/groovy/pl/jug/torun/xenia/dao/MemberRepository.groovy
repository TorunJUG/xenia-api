package pl.jug.torun.xenia.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.model.Member

/**
 * Created by mephi_000 on 06.09.14.
 */
interface MemberRepository  extends JpaRepository<Member, Long>{
    
    
}
