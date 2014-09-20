package pl.jug.torun.xenia.rest

import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import pl.jug.torun.xenia.dao.DrawRepository
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.model.Draw

import javax.transaction.Transactional

/**
 * Created by mephi_000 on 06.09.14.
 */
@Service
class DrawService implements  DrawServiceInterface{

    
    final EventRepository eventRepository
    final DrawRepository drawRepository
    
    @Autowired
    public DrawService(EventRepository eventRepository, DrawRepository drawRepository) {
        this.drawRepository = drawRepository
        this.eventRepository = eventRepository
    }
    
    @Transactional
    public Draw draw(long eventId, long giveAwayId) {
        def event = eventRepository.getOne(eventId)
        def giveAway = event.giveAways.find { it.id = giveAwayId }
        def confirmed = giveAway.draws.count { it.confirmed }
        if (confirmed < giveAway.amount) {
            def attendeesAlreadyWon = giveAway.draws.findAll {it.confirmed}.attendee
            def attendees = event.attendees - attendeesAlreadyWon
        
            def winner = attendees.get(new Random().nextInt(attendees.size()))
            def draw = new Draw(attendee: winner, confirmed: false, drawDate: LocalDateTime.now())
            drawRepository.save(draw)
            giveAway.draws.add(draw)
            eventRepository.save(event)

            return draw
        }
        
        return null
    }

    @Override
    def confirmDraw( long eventId, long giveAwayId, long id) {
        def draw = drawRepository.getOne(id)
        draw.confirmed = true
        drawRepository.save(draw)
    }
}
