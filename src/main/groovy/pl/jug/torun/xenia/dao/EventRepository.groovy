package pl.jug.torun.xenia.dao

import org.springframework.data.jpa.repository.JpaRepository
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.Prize

import javax.persistence.Entity

/**
 * Created by mephi_000 on 06.09.14.
 */

public interface EventRepository extends JpaRepository<Event, Long>{

}