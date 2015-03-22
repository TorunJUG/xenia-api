package pl.jug.torun.xenia.rest

import pl.jug.torun.xenia.model.Draw
import pl.jug.torun.xenia.model.GiveAway

/**
 * Created by mephi_000 on 06.09.14.
 */
public interface DrawServiceInterface {
    public Draw draw(long eventId, long giveAwayId)
    public Draw draw(long id, long eventId, long giveAwayId)
    
    public void draw(long eventId)

    def confirmDraw(long id, long eventId, long giveAwayId)
    def confirmDraws(long eventId)
}