package pl.jug.torun.xenia.draw

import groovy.util.logging.Slf4j
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import pl.jug.torun.xenia.events.Event

import javax.servlet.http.HttpServletResponse
import java.nio.charset.Charset

@Slf4j
@RestController
@RequestMapping(value = "/events/{id}/giveaways/result", produces = "application/json")
final class DrawResultController {

    private final DrawResultRepository drawResultRepository

    @Autowired
    DrawResultController(DrawResultRepository drawResultRepository) {
        this.drawResultRepository = drawResultRepository
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<DrawResult> listDrawResults(@PathVariable("id") Event event) {
        return drawResultRepository.findAllByEvent(event)
    }

    @RequestMapping(value = "/grouped", method = RequestMethod.GET)
    public Map<Long, List<String>> listGroupedDrawResults(@PathVariable("id") Event event) {
        return drawResultRepository.findAllByEvent(event)
                .groupBy { it.giveAway.id }
                .collectEntries { [(it.key): it.value.collect { result -> result.member.name } ]}
    }

    @RequestMapping(value = "/csv", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void downloadCsv(@PathVariable("id") Event event, final HttpServletResponse response) {
        log.debug 'Generating CSV with draw results...'

        List<DrawResult> results = drawResultRepository.findAllByEvent(event)

        String csv = "\"Member ID\",\"Won prize\",\"Member name\",\"Member e-mail\"\n"

        csv = results.inject(csv) { acc, it ->
            acc + String.format('%d,"%s","%s","%s"%n',
                    it.member.id,
                    it.giveAway.prize.name?.replaceAll('"', "'"),
                    it.member.name?.replaceAll('"', "'"),
                    it.member.email?.replaceAll('"', "'")
            )
        }

        String filename = "Meetup-${event.id}-draw-results.csv"

        response.setContentType("text/csv")
        response.setHeader("Content-Disposition", "attachment; filename=${filename}")

        IOUtils.copy(new ByteArrayInputStream(csv.getBytes(Charset.forName("UTF-8"))), response.getOutputStream())
    }
}
