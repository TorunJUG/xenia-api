package pl.jug.torun.xenia

import pl.jug.torun.xenia.meetup.MeetupRestTemplate
import spock.lang.Specification
import spock.lang.Subject

class MeetupConfigSpec extends Specification {

    @Subject
    private final MeetupConfig config = new MeetupConfig()

    def "should return MeetupRestTemplate initialized with injected key and groupUrlName"() {
        given:
        config.setKey(meetupKey)
        config.setGroupUrlName(groupNameUrl)

        when:
        MeetupRestTemplate restTemplate = config.meetupRestTemplate()

        then:
        restTemplate.getMeetupKey() == meetupKey

        and:
        restTemplate.getMeetupGroupNameUrl() == groupNameUrl

        where:
        meetupKey                                               || groupNameUrl
        UUID.randomUUID().toString()                            || "Meetup-Testing"
        UUID.randomUUID().toString()                            || "Torun-JUG"
        UUID.randomUUID().toString()                            || "Some-Random-Name"
    }
}
