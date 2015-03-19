package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import pl.jug.torun.xenia.dao.DrawRepository
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.dao.GiveAwayRepository
import pl.jug.torun.xenia.dao.MeetupMemberRepository
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.meetup.MeetupClient
import pl.jug.torun.xenia.model.Draw
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.model.Member

import javax.transaction.Transactional

/**
 * Created by mephi_000 on 06.09.14.
 */
@Service
class DrawService implements DrawServiceInterface {


    final EventRepository eventRepository
    final DrawRepository drawRepository
    final GiveAwayRepository giveAwayRepository
    private MeetupMemberRepository meetupMemberRepository
    @Autowired
    MeetupClient meetupClient

    @Autowired
    public DrawService(EventRepository eventRepository, DrawRepository drawRepository, GiveAwayRepository giveAwayRepository,
                       MeetupMemberRepository meetupMemberRepository) {
        this.meetupMemberRepository = meetupMemberRepository
        this.giveAwayRepository = giveAwayRepository
        this.drawRepository = drawRepository
        this.eventRepository = eventRepository
    }

    @Transactional
    public Draw draw(long eventId, long giveAwayId) {
        def event = eventRepository.getOne(eventId)
        def giveAway = giveAwayRepository.getOne(giveAwayId)
        def confirmed = giveAway.draws.count { it.confirmed }
        if (confirmed < giveAway.amount) {

            def attendees = getAvailableMembersForDraw(event, giveAway)
            def winner = attendees.get(new Random().nextInt(attendees.size()))
            def draw = new Draw(attendee: winner, confirmed: false, drawDate: LocalDateTime.now())
            drawRepository.save(draw)
            giveAway.draws.add(draw)
            eventRepository.save(event)

            return draw
        }

        return null
    }

    @Transactional
    public void draw(long eventId) {
        def event = eventRepository.getOne(eventId)
        event.giveAways.each { giveAway ->
            def drew = giveAway.draws.size()
            def left = giveAway.amount - drew
            if (left > 0)
                (1..left).each {
                    def attendees = getAvailableMembersForDraw(event, giveAway)
                    def winner = attendees.get(new Random().nextInt(attendees.size()))
                    def draw = new Draw(attendee: winner, confirmed: false, drawDate: LocalDateTime.now())
                    drawRepository.save(draw)
                    giveAway.draws.add(draw)

                }
        }
    }


@Override
def confirmDraw(long eventId, long giveAwayId, long id) {
    def draw = drawRepository.getOne(id)
    draw.confirmed = true
    drawRepository.save(draw)
    try {
        meetupClient.sendGiveawayConfirmation(meetupMemberRepository.getByMember(draw.attendee),
                giveAwayRepository.getOne(giveAwayId).prize)
    } catch (Exception e) {
        //TODO: no problem if no mail sent for now
    }
}

def getAvailableMembersForDraw(Event event, GiveAway giveAway) {
    List<Member> memberWhoWonThePrize = (giveAwayRepository.findByPrize(giveAway.prize).draws.findAll {
        it.confirmed
    }).attendee.flatten()
//        List<Member> membersWhoWonOnEvent = event.giveAways.draws.findAll { it.confirmed  }.attendee.flatten()

//        return event.attendees - membersWhoWonOnEvent - memberWhoOneThePrize
    return event.attendees - event.giveAways.draws.attendee.flatten() - memberWhoWonThePrize

}

}
