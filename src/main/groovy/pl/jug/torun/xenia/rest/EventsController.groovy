package pl.jug.torun.xenia.rest

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.Member
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.rest.dto.EventResponse
import pl.jug.torun.xenia.rest.dto.EventsResponse
import pl.jug.torun.xenia.service.EventsService

import javax.servlet.http.HttpServletResponse
import java.nio.charset.Charset

/**
 * Created by mephi_000 on 06.09.14.
 */
@Slf4j
@RestController
@RequestMapping("/events")
class EventsController {

    static final String OK_RESPONSE = '{"status":"OK"}'

    @Autowired
    EventsService eventsService

    @Autowired
    ObjectMapper objectMapper

    @RequestMapping(method = RequestMethod.GET, produces = ["application/json"])
    EventsResponse getEvents() {
        return new EventsResponse(events: eventsService.findAll().collect {
            event -> new EventResponse(event)
        })
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = ["application/json"])
    String refresh() {
        eventsService.refreshEvents()
        return OK_RESPONSE
    }

    @RequestMapping(value = '/export', method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    void jsonExport(final HttpServletResponse response) {

        List<Event> events = eventsService.findAll()

        def records = events.collect { event ->
            event.giveAways.collect { giveaway ->
                giveaway.draws.findAll { it.confirmed }.collect { draw ->
                    new ExportedDrawResult(event, giveaway.prize, draw.attendee)
                }
            }
        }.flatten()

        String json = objectMapper.writeValueAsString(records)

        response.setContentType("application/json")
        response.setHeader("Content-Disposition", "attachment; filename=xenia-1.0.dump.json")

        IOUtils.copy(new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8"))), response.getOutputStream())
    }

    static class ExportedDrawResult {
        final Long eventId
        final String member
        final String prize
        final String prizeImage

        ExportedDrawResult(final Event event, final Prize prize, final Member member) {
            this.eventId = event.meetupId
            this.member = member.displayName
            this.prize = prize.name
            this.prizeImage = prize.imageUrl
        }
    }

}
