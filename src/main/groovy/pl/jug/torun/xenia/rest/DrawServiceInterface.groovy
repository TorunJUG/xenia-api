package pl.jug.torun.xenia.rest

import pl.jug.torun.xenia.model.Draw

/**
 * Created by mephi_000 on 06.09.14.
 */
public interface DrawServiceInterface {
    public Draw draw(long eventId, long giveAwayId)

    def confirmDraw(long id, long eventId, long giveAwayId)
}