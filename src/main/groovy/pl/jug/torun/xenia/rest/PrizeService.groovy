package pl.jug.torun.xenia.rest

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import pl.jug.torun.xenia.dao.GiveAwayRepository
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeRequest

import javax.transaction.Transactional

@Slf4j
@Service
final class PrizeService implements PrizeServiceInterface {

    private final PrizeRepository prizeRepository
    private final GiveAwayRepository giveAwayRepository

    @Autowired
    PrizeService(PrizeRepository prizeRepository, GiveAwayRepository giveAwayRepository) {
        this.prizeRepository = prizeRepository
        this.giveAwayRepository = giveAwayRepository
    }

    @Override
    Prize get(long id) {
        return prizeRepository.findOne(id)
    }

    @Override
    @Transactional
    Prize create(final PrizeRequest prizeRequest) {
        if (prizeRepository.countByName(prizeRequest.name) > 0) {
            throw new IllegalArgumentException("Prize with name '${prizeRequest.name}' already exists")
        }

        final Prize prize = prizeRequest.toPrize()

        return prizeRepository.save(prize)
    }

    @Override
    @Transactional
    Prize update(long id, final PrizeRequest prizeRequest) {
        if (prizeRepository.countByNameAndIdNot(prizeRequest.name, id) > 0) {
            throw new IllegalArgumentException("Prize with name '${prizeRequest.name}' already exists")
        }

        final Prize prize = prizeRepository.findOne(id)

        Assert.notNull prize, "Prize with id ${id} not found"

        prize.name = prizeRequest.name ?: prize.name
        prize.producer = prizeRequest.producer ?: prize.producer
        prize.sponsorName = prizeRequest.sponsorName ?: prize.sponsorName
        prize.imageUrl = prizeRequest.imageUrl ?: prize.imageUrl

        return prizeRepository.save(prize)
    }

    @Override
    @Transactional
    void delete(long id) {
        long giveaways = giveAwayRepository.countByPrizeId(id)
        if (giveaways == 0l) {
            prizeRepository.delete(id)
        } else {
            Prize prize = prizeRepository.findOne(id)
            prize.deleted = true
            prizeRepository.save(prize)
        }
    }
}
