package pl.jug.torun.xenia.draw

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import pl.jug.torun.xenia.meetup.Member

import javax.persistence.*

@Entity
@EqualsAndHashCode
@ToString(includePackage = false, includeFields = true)
final class DrawResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id

    @ManyToOne
    GiveAway giveAway

    @ManyToOne
    Member member

    private DrawResult() { }

    public DrawResult(GiveAway giveAway, Member member) {
        this.giveAway = giveAway
        this.member = member
    }
}
