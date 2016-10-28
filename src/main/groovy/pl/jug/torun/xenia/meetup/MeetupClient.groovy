package pl.jug.torun.xenia.meetup

import groovy.transform.ToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import pl.jug.torun.xenia.events.Event

@Component
final class MeetupClient {

    private final MeetupRestTemplate restTemplate

    @Autowired
    MeetupClient(MeetupRestTemplate restTemplate) {
        this.restTemplate = restTemplate
    }

    public List<Event> getAllEvents() {
        return restTemplate.getForObject("/events?only=id,name,time&status=upcoming,past", EventsResponse).getResults() ?: []
    }

    public List<Member> getAllEventAttendees(long id) {
        return restTemplate.getForObject(String.format(
                "/rsvps?event_id=%d&only=%s&rsvp=%s",
                id,
                "member,member_photo,answers",
                "yes"
        ), MembersResponse).getResults() ?: []
    }

    @ToString(includePackage = false)
    private static class EventsResponse {
        List<Event> results = []
    }


    @ToString(includePackage = false)
    private static class MembersResponse {
        List<Member> results = []
    }
}