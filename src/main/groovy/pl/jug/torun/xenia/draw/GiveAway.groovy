package pl.jug.torun.xenia.draw

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.ColumnDefault
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.prizes.Prize

import javax.persistence.*

@Entity
@EqualsAndHashCode
@ToString(includePackage = false, includeFields = true)
final class GiveAway {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id

    @ManyToOne
    Prize prize

    @JsonIgnore
    @ManyToOne
    Event event

    int amount

    @ColumnDefault("false")
    boolean emailRequired

    private GiveAway() {}

    GiveAway(Prize prize, Event event, int amount, boolean emailRequired) {
        this.prize = prize
        this.event = event
        this.amount = amount
        this.emailRequired = emailRequired
    }
}
