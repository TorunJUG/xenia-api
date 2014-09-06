package pl.jug.torun.xenia.dao

import org.springframework.data.jpa.repository.JpaRepository
import pl.jug.torun.xenia.model.Draw
import pl.jug.torun.xenia.model.Event

/**
 * Created by mephi_000 on 06.09.14.
 */
interface DrawRepository  extends JpaRepository<Draw, Long> {
}
