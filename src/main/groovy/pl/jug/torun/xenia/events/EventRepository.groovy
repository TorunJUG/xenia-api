package pl.jug.torun.xenia.events

import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository extends JpaRepository<Event, Long> {
}