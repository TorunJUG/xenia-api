package pl.jug.torun.xenia.rest

import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.PrizeRequest

/**
 * Created by wololock on 18.05.16.
 */
interface PrizeServiceInterface {
    Prize get(long id)
    Prize create(final PrizeRequest prizeRequest)
    Prize update(long id, final PrizeRequest prizeRequest)
    void delete(long id)
}