package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.mockito.Mock
import pl.jug.torun.xenia.dao.DrawRepository
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.dao.GiveAwayRepository
import pl.jug.torun.xenia.model.Draw
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.model.Member
import pl.jug.torun.xenia.model.Prize
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

/**
 * Created by mephi_000 on 2014-09-15.
 */

class DrawServiceTest extends Specification {

    @Mock
    EventRepository eventRepository
    @Mock
    DrawRepository drawRepository
    @Mock
    GiveAwayRepository giveAwayRepository


    def "can draw on new giveaway"() {
        given:
        def noOfAttendees = 2
        def drawService = getServiceWithSingleGiveAwayEvent(noOfAttendees)
        when:
        Draw draw = drawService.draw(1L, 1)
        then:
        draw.attendee.id in 1L..noOfAttendees
    }

    def "cannot draw on confirmed single draw giveaway"() {

        given:
        def noOfAttendees = 2
        def drawService = getServiceWithSingleGiveAwayEvent(noOfAttendees)
        def draw = drawService.draw(1L, 1)
        confirmDraw(draw)
        when:
        def nextDraw = drawService.draw(1L, 1L)

        then:
        nextDraw == null
    }

    def "can draw two times on confirmed single draw giveaway with two items"() {

        given:
        def noOfAttendees = 2
        def noOfItems = 2
        def drawService = getServiceWithSingleGiveAwayEvent(noOfAttendees, noOfItems)
        def draw = drawService.draw(1L, 1)
        confirmDraw(draw)
        when:
        def nextDraw = drawService.draw(1L, 1L)

        then:
        nextDraw != null
        nextDraw.attendee.id in 1L..noOfAttendees
    }

    @Unroll("test repeated #i time")
    def "can draw two times on confirmed single draw giveaway with two items and both attendes should win"() {

        given:
        
        def noOfAttendees = 2
        def noOfItems = 2
        def drawService = getServiceWithSingleGiveAwayEvent(noOfAttendees, noOfItems)
        when:

        def firstDraw = drawService.draw(1L, 1L)
        confirmDraw(firstDraw)
        getServiceWithSingleGiveAwayEvent(noOfAttendees, noOfItems, [firstDraw])
        def secondDraw = drawService.draw(1L, 1L)
        confirmDraw(secondDraw)
        getServiceWithSingleGiveAwayEvent(noOfAttendees, noOfItems, [firstDraw, secondDraw])
        def thirdDraw = drawService.draw(1L, 1L)

        then:
        firstDraw != null
        secondDraw != null
        thirdDraw == null
        [firstDraw, secondDraw].attendee.id as Set == (1..noOfAttendees) as Set
         
        where:
            i << (1..10) //hack for randomize - should be done with mockRandomizer
    }
    

    
    private void confirmDraw(Draw draw) {
        draw.confirmed = true
        when(drawRepository.getOne(1L)).thenReturn(draw)
    }


    def getServiceWithSingleGiveAwayEvent(int noOfAttendees, int noOfPrizesPerGiveAway = 1, List<Draw> draws = []) {
        initMocks(this)
        noOfAttendees = 2
        when(eventRepository.getOne(1L)).thenReturn(eventWith(1, 1, noOfAttendees, [1: new Prize(id: 1, name: "prize")], noOfPrizesPerGiveAway, draws))
        return new DrawService(eventRepository, drawRepository, giveAwayRepository)
    }

    def eventWith(int id, int noOfGiveaways, int noOfMembers, Map<Integer, Prize> prizes, int drawsNo, List<Draw> draws) {
        List<Member> members = (1..noOfMembers).collect {
            new Member(id: it, displayName: "attendee$it", photoUrl: "no-photo")
        }

        List<GiveAway> giveAways = (1..noOfGiveaways).collect {
            new GiveAway(amount: drawsNo, id: it, prize: prizes[it], draws: draws)
        }
        return new Event(id: id, attendees: members, startDate: LocalDateTime.now(),
                endDate: LocalDateTime.now().plusHours(1), giveAways: giveAways, title: 'testEvent')

    }

}
