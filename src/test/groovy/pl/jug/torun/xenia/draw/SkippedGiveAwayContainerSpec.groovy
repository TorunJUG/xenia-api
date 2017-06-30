package pl.jug.torun.xenia.draw

import pl.jug.torun.xenia.meetup.Member
import spock.lang.Specification

class SkippedGiveAwayContainerSpec extends Specification {

    def "should add skipped members for same giveaway"() {
        given:
        SkippedGiveAwayContainer container = new SkippedGiveAwayContainer()
        Member firstMember = new Member(id: 100)
        Member nextMember = new Member(id: 200)
        GiveAway giveAway = new GiveAway(id: 1000)

        when:
        container.addSkippedMember(giveAway, firstMember)
        container.addSkippedMember(giveAway, nextMember)

        then:
        container.getMembersByGiveAway(giveAway).with {
            it.size() == 2 && it[0] == firstMember && it[1] == nextMember
        }
    }

    def "should add skipped members for different giveaways"() {
        given:
        SkippedGiveAwayContainer container = new SkippedGiveAwayContainer()
        Member firstMember = new Member(id: 100)
        Member nextMember = new Member(id: 200)
        GiveAway firstGiveAway = new GiveAway(id: 1000)
        GiveAway nextGiveAway = new GiveAway(id: 2000)

        when:
        container.addSkippedMember(firstGiveAway, firstMember)
        container.addSkippedMember(nextGiveAway, nextMember)

        then:
        container.getMembersByGiveAway(firstGiveAway).with {
            it.size() == 1 && it[0] == firstMember
        }
        container.getMembersByGiveAway(nextGiveAway).with {
            it.size() == 1 && it[0] == nextMember
        }
    }

}
