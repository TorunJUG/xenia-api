package pl.jug.torun.xenia.draw

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import pl.jug.torun.xenia.events.Attendee
import pl.jug.torun.xenia.events.AttendeeRepository
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.meetup.Member
import pl.jug.torun.xenia.meetup.MemberRepository

import static org.apache.commons.lang.StringUtils.isNotBlank

@Slf4j
@Service
class DrawService {
    private final DrawResultRepository drawResultRepository
    private final AttendeeRepository attendeeRepository
    private final MemberRepository memberRepository
    private final SkippedGiveAwayContainer skippedGiveAwayContainer

    @Autowired
    DrawService(DrawResultRepository drawResultRepository, SkippedGiveAwayContainer skippedGiveAwayContainer, AttendeeRepository attendeeRepository,
                MemberRepository memberRepository) {
        this.drawResultRepository = drawResultRepository
        this.skippedGiveAwayContainer = skippedGiveAwayContainer
        this.attendeeRepository = attendeeRepository
        this.memberRepository = memberRepository
    }

    @Transactional
    public DrawResult drawWinnerCandidate(final GiveAway giveAway) {
        Assert.state giveAway.amount > drawResultRepository.countAllByGiveAway(giveAway), 'You cannot draw more giveaways for this prize!'

        List<Attendee> attendees = attendeeRepository.findAllPresentMembersAtTheEvent(giveAway.event.id)
        Assert.state !attendees.empty, 'No one attended this event, you cannot draw a giveaway'

        List<Member> members = acceptAttendeesWhoDidNotWinAnyPrizeYetDuringTheEventAndDidNotWinThisGiveawayBefore(attendees, giveAway)

        log.debug 'Drawing from {} members...', members.size()
        log.debug 'Members = {}', members.collect { it.name }

        Member winner = members.get(new Random().nextInt(members.size()))

        return new DrawResult(giveAway, winner)
    }

    private List<Member> acceptAttendeesWhoDidNotWinAnyPrizeYetDuringTheEventAndDidNotWinThisGiveawayBefore(List<Attendee> attendees, GiveAway giveAway) {
        List<Member> members = attendees.collect { it.member }

        List<Member> winnersInCurrentEvent = drawResultRepository.findAllByEvent(giveAway.event).collect { it.member }
        log.debug 'In this event draw {} members have won already...', winnersInCurrentEvent.size()
        members.removeAll(winnersInCurrentEvent)

        List<Member> membersWhoHaveWonThisPrizeAlready = drawResultRepository.findAllByPrize(giveAway.prize).collect { it.member }
        log.debug '{} members have won this prize already...', membersWhoHaveWonThisPrizeAlready.size()
        members.removeAll(membersWhoHaveWonThisPrizeAlready)

        Set<Member> membersWithSkippedGiveAway = skippedGiveAwayContainer.getMembersByGiveAway(giveAway)
        log.debug '{} members have skipped this prize already...', membersWithSkippedGiveAway.size()
        members.removeAll(membersWithSkippedGiveAway)

        if (giveAway.emailRequired) {
            members = members.findAll { isNotBlank(it.email) }
        }
        return members
    }

    @Transactional
    public DrawResult confirmWinner(final GiveAway giveAway, long memberId) {
        Assert.state giveAway.amount > drawResultRepository.countAllByGiveAway(giveAway), 'You cannot draw more giveaways for this prize!'

        Member member = memberRepository.findOne(memberId)
        DrawResult drawResult = new DrawResult(giveAway, member)
        return drawResultRepository.save(drawResult)
    }

    @Transactional
    public void markMemberAsAbsentForCurrentDraw(final Member member, final Event event) {
        log.debug 'Member {} is not present at the event, marking as absent to skip this member in next draw...', member.name
        Attendee attendee = attendeeRepository.findOne(new Attendee.Id(event, member))
        if (attendee) {
            attendee.absent = true
            attendeeRepository.save(attendee)
        }
    }

    public void setGiveAwaySkippedForMember(Member member, GiveAway giveAway) {
        log.debug 'Member {} skipped the draw result for {}', member, giveAway
        skippedGiveAwayContainer.addSkippedMember(giveAway, member)
    }

}
