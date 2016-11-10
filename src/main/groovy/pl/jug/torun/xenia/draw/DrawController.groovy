package pl.jug.torun.xenia.draw

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jug.torun.xenia.meetup.Member

import javax.validation.Valid

@Slf4j
@RestController
@RequestMapping(value = "/events/{id}/giveaways/{giveAway}/draw", produces = "application/json")
final class DrawController {

    private final DrawService drawService

    @Autowired
    DrawController(DrawService drawService) {
        this.drawService = drawService
    }

    @RequestMapping(method = RequestMethod.GET)
    public DrawResult drawWinnerCandidate(@PathVariable("giveAway") GiveAway giveAway, @RequestParam(value = "absent", required = false) Member absentMember,
                                          @RequestParam(value = "skipped", required = false) Member skippedMember) {
        if (absentMember) {
            drawService.markMemberAsAbsentForCurrentDraw(absentMember, giveAway.event)
        } else if (skippedMember) {
            drawService.setGiveAwaySkippedForMember(skippedMember, giveAway)
        }

        return drawService.drawWinnerCandidate(giveAway)
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public DrawResult confirmWinner(@PathVariable("giveAway") GiveAway giveAway, @Valid @RequestBody ConfirmWinnerRequest request) {
        return drawService.confirmWinner(giveAway, request.memberId)
    }

    static class ConfirmWinnerRequest {
        long memberId
    }
}
