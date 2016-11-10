package pl.jug.torun.xenia.draw

import org.springframework.stereotype.Component
import pl.jug.torun.xenia.meetup.Member

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

@Component
class SkippedGiveAwayContainer {

    Map<GiveAway, Set<Member>> skippedGiveWays = new ConcurrentHashMap<>()

    Set<Member> getMembersByGiveAway(GiveAway giveaway) {
        (skippedGiveWays[giveaway] ?: new HashSet<>()).asImmutable()
    }

    void addSkippedMember(GiveAway giveAway, Member member) {
        Set<Member> members = skippedGiveWays[giveAway]
        if (!members) {
            members = new ConcurrentSkipListSet<Member>({ m1, m2 -> m1.id.compareTo(m2.id) })
            skippedGiveWays.put(giveAway, members)
        }
        members.add(member)
    }
}