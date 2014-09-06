package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jug.torun.xenia.dao.EventRepository
import pl.jug.torun.xenia.dao.GiveAwayRepository
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.GiveAway
import pl.jug.torun.xenia.rest.dto.GiveAwayRequest

/**
 * Created by mephi_000 on 06.09.14.
 */
@Service
class GiveAwayService implements GiveAwayServiceInterface{

    @Autowired
    EventRepository eventRepository

    @Autowired
    PrizeRepository prizeRepository
    
    @Autowired
    GiveAwayRepository giveAwayRepository
    
    @Override
    @Transactional
    GiveAway saveGiveAway(long eventId, GiveAwayRequest request) {
        def giveAway = new GiveAway(amount: request.amount)
        giveAway.prize = prizeRepository.getOne(request.prizeId)
        def event = eventRepository.getOne(eventId)
        giveAwayRepository.save(giveAway)
        event.giveAways.add(giveAway)

        eventRepository.save(event)
        return giveAway
    }
}
