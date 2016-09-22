package pl.jug.torun.xenia.draw

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.*
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.prizes.PrizeRepository

import javax.validation.Valid
import javax.validation.constraints.Min

@RestController
@RequestMapping(value = "/events/{id}/giveaways", produces = "application/json")
class GiveAwayController {

    private final GiveAwayRepository giveAwayRepository
    private final PrizeRepository prizeRepository
    private final DrawResultRepository drawResultRepository

    @Autowired
    GiveAwayController(GiveAwayRepository giveAwayRepository, PrizeRepository prizeRepository, DrawResultRepository drawResultRepository) {
        this.giveAwayRepository = giveAwayRepository
        this.prizeRepository = prizeRepository
        this.drawResultRepository = drawResultRepository
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<GiveAway> listAll(@PathVariable("id") Event event) {
        return giveAwayRepository.findAllByEvent(event)
    }

    @RequestMapping(value = "/prizes/queue", method = RequestMethod.GET)
    public List<GiveAway> prizesQueue(@PathVariable("id") Event event) {
        List<GiveAway> giveAways = giveAwayRepository.findAllByEvent(event)
        List<DrawResult> results = drawResultRepository.findAllByEvent(event)

        giveAways = giveAways.collect { [it] * it.amount }.flatten()

        results.collect { it.giveAway }.each {
            int index = giveAways.indexOf(it)
            if (index >= 0) {
                giveAways.remove(index)
            }
        }
        return giveAways
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public GiveAway create(@PathVariable("id") Event event, @Valid @RequestBody CreateGiveAwayRequest request) {
        GiveAway giveAway = new GiveAway(
                event: event,
                prize: prizeRepository.findOne(request.prize),
                amount: request.amount,
                emailRequired: request.emailRequired
        )

        return giveAwayRepository.save(giveAway)
    }

    @RequestMapping(value = "/{giveAway}", method = RequestMethod.PUT, consumes = "application/json")
    public GiveAway update(@PathVariable("giveAway") GiveAway giveAway, @Valid @RequestBody UpdateGiveAwayRequest request) {
        Assert.notNull giveAway, "GiveAway with given id does not exist"

        giveAway.amount = request.amount
        giveAway.emailRequired = request.emailRequired != null ? request.emailRequired : giveAway.emailRequired

        return giveAwayRepository.save(giveAway)
    }

    @RequestMapping(value = "/{giveAway}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("giveAway") GiveAway giveAway) {
        giveAwayRepository.delete(giveAway)
    }

    static class CreateGiveAwayRequest {
        @Min(1L)
        long prize
        @Min(0L)
        int amount
        boolean emailRequired
    }

    static class UpdateGiveAwayRequest {
        @Min(0L)
        int amount
        Boolean emailRequired
    }
}
