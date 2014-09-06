package pl.jug.torun.xenia.meetup

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.joda.time.LocalDateTime
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import pl.jug.torun.xenia.model.Event
import pl.jug.torun.xenia.model.Member

@Service
@ConfigurationProperties(prefix = "meetup")
class MeetupClient {

    private static final String MEETUP_API_HOST = 'https://api.meetup.com'

    String key

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

    List<Member> findAllForEvent(Long id) {
        RESTClient request = new RESTClient(MEETUP_API_HOST)

        Map params = [key: key, group_urlname: groupUrlName, event_id: id, rsvp: 'yes']

        HttpResponseDecorator response = request.get(
                path: '/2/rsvps.json',
                query: params,
                contentType: 'application/json'
        ) as HttpResponseDecorator

        return response?.data?.results?.collect { MemberConverter.createFromJSON(it) }
    }

    private static class EventConverter {
        static Event createFromJSON(Map json) {
            LocalDateTime startDate = new LocalDateTime(Long.valueOf(json?.time))
            LocalDateTime lastUpdate = new LocalDateTime(Long.valueOf(json?.updated))

            return new Event(
                    title: json?.name,
                    meetupId: Long.valueOf(json?.id),
                    startDate: startDate,
                    endDate: json?.duration ? startDate.plusMillis(Integer.valueOf(json?.duration)) : startDate.plusHours(3),
                    updatedAt: lastUpdate
            )
        }
    }

    private static class MemberConverter {
        static Member createFromJSON(Map json) {
            return new Member(
                    displayName: json?.member?.name,
                    meetupId: json?.member?.member_id,
                    photoUrl: json?.member_photo?.thumb_link
            )
        }
    }
}
