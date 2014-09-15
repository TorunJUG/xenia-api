package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.mockito.Mock
import pl.jug.torun.xenia.dao.DrawRepository
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.model.Draw
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.model.Member
import pl.jug.torun.xenia.model.Prize
import spock.lang.Specification

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
    
    
    
    def "can draw on new giveaway"() {
        given:
            def noOfAttendees = 2
            def drawService = getServiceWithSingleGiveAwayEvent(noOfAttendees)
        when:
            Draw draw = drawService.draw(1L, 1)
        then:
            draw.attendee.id in 1L .. noOfAttendees
    }
    
    def "cannot draw on confirmed single draw giveaway"() {

        given:
            def noOfAttendees = 2
            def drawService = getServiceWithSingleGiveAwayEvent(noOfAttendees)
            def draw = drawService.draw(1L, 1)
            draw.confirmed = true
            when(drawRepository.getOne(1L)).thenReturn(draw)
        when:
             def nextDraw = drawService.draw(1L, 1L)
            
        then:
            nextDraw == null
    }
    
    
    
    
    def getServiceWithSingleGiveAwayEvent(int noOfAttendees) {
        initMocks(this)
        noOfAttendees = 2
        when(eventRepository.getOne(1L)).thenReturn(eventWith(1,1, noOfAttendees, [1:new Prize(id: 1, name: "prize")], 1))
        return new DrawService(eventRepository, drawRepository)
        
        
    }
    
    def eventWith(int id, int noOfGiveaways, int noOfMembers, Map<Integer, Prize> prizes, int draws) {
        List<Member> members = (1..noOfMembers).collect {
            new Member(id: it, displayName: "attendee$it", photoUrl: "no-photo", meetupId: it)
        }

        List<GiveAway> giveAways = (1..noOfGiveaways).collect {
            new GiveAway(amount: draws, id: it, prize: prizes[it], draws: [])
        }
        return new Event(id: id, attendees: members, startDate: LocalDateTime.now(), 
                endDate: LocalDateTime.now().plusHours(1), giveAways: giveAways, meetupId: id, title: 'testEvent')
        
    }
    
}
