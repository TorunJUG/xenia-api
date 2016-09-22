package pl.jug.torun.xenia.prizes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

@RestController
@RequestMapping(value = "/prizes", produces = "application/json")
class PrizesController {

    private final PrizeRepository prizeRepository

    @Autowired
    PrizesController(PrizeRepository prizeRepository) {
        this.prizeRepository = prizeRepository
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Prize> listAll() {
        return prizeRepository.findAll()
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public Prize create(@Valid @RequestBody Prize prize) {
        return prizeRepository.save(prize)
    }

    @RequestMapping(value ="/active", method = RequestMethod.GET)
    public List<Prize> listActive() {
        return prizeRepository.findAllByInactiveOrderByIdAsc(false)
    }

    @RequestMapping(value ="/inactive", method = RequestMethod.GET)
    public List<Prize> listInactive() {
        return prizeRepository.findAllByInactiveOrderByIdAsc(true)
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public Prize update(@PathVariable("id") long id, @Valid @RequestBody Prize prize) {
        Assert.isTrue prizeRepository.exists(id), "Prize with id ${id} does not exist"
        prize.id = id
        return prizeRepository.save(prize)
    }

    @RequestMapping(value = "/{id}/activate")
    public Prize makeActive(@PathVariable("id") Prize prize) {
        Assert.notNull prize, "Prize with given ID not found"
        prize.inactive = false
        return prizeRepository.save(prize)
    }

    @RequestMapping(value = "/{id}/disable")
    public Prize makeInactive(@PathVariable("id") Prize prize) {
        Assert.notNull prize, "Prize with given ID not found"
        prize.inactive = true
        return prizeRepository.save(prize)
    }
}
