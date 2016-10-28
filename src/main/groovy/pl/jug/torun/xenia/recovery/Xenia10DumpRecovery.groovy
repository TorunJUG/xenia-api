package pl.jug.torun.xenia.recovery

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import pl.jug.torun.xenia.draw.DrawResult
import pl.jug.torun.xenia.draw.DrawResultRepository
import pl.jug.torun.xenia.draw.GiveAway
import pl.jug.torun.xenia.draw.GiveAwayRepository
import pl.jug.torun.xenia.events.Attendee
import pl.jug.torun.xenia.events.AttendeeRepository
import pl.jug.torun.xenia.events.Event
import pl.jug.torun.xenia.events.EventRepository
import pl.jug.torun.xenia.meetup.MeetupClient
import pl.jug.torun.xenia.meetup.Member
import pl.jug.torun.xenia.meetup.MemberRepository
import pl.jug.torun.xenia.prizes.Prize
import pl.jug.torun.xenia.prizes.PrizeRepository

@Slf4j
@Component
@Order(0)
@Profile("!test")
class Xenia10DumpRecovery implements InitializingBean {

    private static final String XENIA_10_DUMP_FILE = "/recover/xenia-1.0.dump.json"

    private final MeetupClient meetupClient
    private final EventRepository eventRepository
    private final MemberRepository memberRepository
    private final AttendeeRepository attendeeRepository
    private final PrizeRepository prizeRepository
    private final GiveAwayRepository giveAwayRepository
    private final DrawResultRepository drawResultRepository
    private final ObjectMapper objectMapper

    @Autowired
    Xenia10DumpRecovery(MeetupClient meetupClient, EventRepository eventRepository, MemberRepository memberRepository, AttendeeRepository attendeeRepository, PrizeRepository prizeRepository, GiveAwayRepository giveAwayRepository, DrawResultRepository drawResultRepository, ObjectMapper objectMapper) {
        this.meetupClient = meetupClient
        this.eventRepository = eventRepository
        this.memberRepository = memberRepository
        this.attendeeRepository = attendeeRepository
        this.prizeRepository = prizeRepository
        this.giveAwayRepository = giveAwayRepository
        this.drawResultRepository = drawResultRepository
        this.objectMapper = objectMapper
    }

    @Override
    void afterPropertiesSet() throws Exception {
        recover()
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void recover() {
        InputStream inputStream = getClass().getResourceAsStream(XENIA_10_DUMP_FILE)

        if (inputStream?.available()) {
            log.debug 'File {} exists, can continue recovery process...', XENIA_10_DUMP_FILE

            if (eventRepository.count() == 0) {
                log.debug 'There are no events synchronized yet, synchronizing with Meetup...'

                eventRepository.save(meetupClient.getAllEvents())
                log.debug 'Synchronized {} events...', eventRepository.count()

                eventRepository.findAll().each { event ->
                    log.debug 'Synchronizing "{}" attendees...', event.name

                    List<Member> attendees = meetupClient.getAllEventAttendees(event.id)
                    attendees.each { member ->
                        memberRepository.save(member)
                        attendeeRepository.save(new Attendee(event, member))
                    }

                    log.debug '{} attendees synchronized...', attendees.size()
                }

                List<ImportedJsonRow> importedData = objectMapper.readValue(inputStream, new TypeReference<List<ImportedJsonRow>>(){})
                log.debug 'There are {} draw results to import', importedData.size()

                importedData.each { row ->
                    Event event = eventRepository.findOne(row.eventId)
                    if (event) {
                        Member member = memberRepository.findByName(row.member)

                        if (member) {
                            Prize prize = prizeRepository.findByName(row.prize)

                            if (!prize) {
                                prize = prizeRepository.save(new Prize(name: row.prize, imageUrl: row.prizeImage))
                            }

                            GiveAway giveAway = giveAwayRepository.findByPrizeAndEvent(prize, event)

                            if (!giveAway) {
                                giveAway = giveAwayRepository.save(new GiveAway(prize: prize, event: event, amount: 1))
                            } else {
                                giveAway.amount += 1
                                giveAway = giveAwayRepository.save(giveAway)
                            }

                            drawResultRepository.save(new DrawResult(giveAway, member))
                        }
                    }
                }
            }
        }

        log.debug 'Recovery finished!'
    }

    static class ImportedJsonRow {
        Long eventId
        String member
        String prize
        String prizeImage
    }
}
