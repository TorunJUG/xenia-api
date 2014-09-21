package pl.jug.torun.xenia.meetup

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.joda.time.LocalDateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.Member
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.model.meetup.MeetupMember

@Service
@ConfigurationProperties(prefix = "meetup")
class MeetupClient {

    private static final String MEETUP_API_HOST = 'https://api.meetup.com'

    @Value('${meetup.key:""}')
    String key
    @Value('${meetup.code:""}')
    String code

    @Value('${meetup.mail.subject_template:""}')
    String subjectTemplate

    @Value('${meetup.mail.body_template:""}')
    String bodyTemplate


    @Value('${meetup.groupId:""}')
    String groupId

    @Value('${meetup.access_token:""}')
    String token

    @Autowired
    TokenRequester tokenRequester

    String groupUrlName

    List<Event> findAllEvents() {
        RESTClient request = new RESTClient(MEETUP_API_HOST)

        Map params = [key: key, group_urlname: groupUrlName, status: 'upcoming,past']

        HttpResponseDecorator response = request.get(
                path: '/2/events.json',
                query: params,
                contentType: 'application/json'
        ) as HttpResponseDecorator

        return response.data?.results?.collect { EventConverter.createFromJSON(it) }
    }

    List<MeetupMember> findAllAttendeesOfEvent(Long id) {
        RESTClient request = new RESTClient(MEETUP_API_HOST)

        Map params = [key: key, group_urlname: groupUrlName, event_id: id, rsvp: 'yes']

        HttpResponseDecorator response = request.get(
                path: '/2/rsvps.json',
                query: params,
                contentType: 'application/json'
        ) as HttpResponseDecorator

        return response?.data?.results?.collect { MemberConverter.createFromJSON(it) }
    }

    void sendGiveawayConfirmation(MeetupMember member, Prize prize) {
        RESTClient request = new RESTClient(MEETUP_API_HOST)

        Map params = [
                dryrun   : true,
                member_id: member.id, group_id: groupId, access_token: token,
                subject  : String.format(subjectTemplate, prize.name), message: String.format(bodyTemplate, prize.name)
        ]

        def post = request.post(
                path: '/2/message',
                body: params,
                contentType: 'application/x-www-form-urlencoded'
        )
       
    }


        private static class EventConverter {
            static Event createFromJSON(Map json) {
                LocalDateTime startDate = new LocalDateTime(Long.valueOf(json?.time))
                LocalDateTime lastUpdate = new LocalDateTime(Long.valueOf(json?.updated))

                return new Event(
                        title: json?.name,
                        meetupId: json?.id as Long,
                        startDate: startDate,
                        endDate: json?.duration ? startDate.plusMillis(Integer.valueOf(json?.duration)) : startDate.plusHours(3),
                        updatedAt: lastUpdate
                )
            }
        }

        private static class MemberConverter {
            static MeetupMember createFromJSON(Map json) {
                return new MeetupMember(id: json?.member?.member_id,
                        member: new Member(
                                displayName: json?.member?.name,
                                photoUrl: json?.member_photo?.thumb_link
                        )
                )
            }
        }
    }
