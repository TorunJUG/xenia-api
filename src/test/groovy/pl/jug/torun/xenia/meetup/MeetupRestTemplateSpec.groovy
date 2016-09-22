package pl.jug.torun.xenia.meetup

import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse
import spock.lang.Specification
import spock.lang.Subject

class MeetupRestTemplateSpec extends Specification {

    private static final String RANDOM_MEETUP_API_KEY = UUID.randomUUID().toString()
    private static final String MEETUP_NAME_URL = "Meetup-Testing";

    private final ClientHttpRequestFactory requestFactory = Mock(ClientHttpRequestFactory)

    @Subject
    private final MeetupRestTemplate restTemplate = new MeetupRestTemplate(RANDOM_MEETUP_API_KEY, MEETUP_NAME_URL, requestFactory)

    def "should start every request with https://api.meetup.com/2"() {
        when:
        restTemplate.exchange("/status", HttpMethod.GET, new HttpEntity<Object>(), Void)

        then:
        1 * requestFactory.createRequest({ URI it ->
            it.toString().startsWith("https://api.meetup.com/2")
        } as URI, HttpMethod.GET) >> { URI uri, HttpMethod method ->
            ClientHttpRequest request = new MockClientHttpRequest(method, uri)
            request.setResponse(new MockClientHttpResponse("{}".bytes, HttpStatus.OK))
            return request
        }
    }

    def "should pass meetup key as a URL parameter with every request"() {
        when:
        restTemplate.exchange("/events", HttpMethod.GET, new HttpEntity<Object>(), Void)

        then:
        1 * requestFactory.createRequest({ URI it ->
            it.toString().contains("key=${RANDOM_MEETUP_API_KEY}")
        } as URI, HttpMethod.GET) >> { URI uri, HttpMethod method ->
            ClientHttpRequest request = new MockClientHttpRequest(method, uri)
            request.setResponse(new MockClientHttpResponse("{}".bytes, HttpStatus.OK))
            return request
        }
    }

    def "should pass group_urlname as a URL parameter with every request"() {
        when:
        restTemplate.exchange("/rsvps", HttpMethod.GET, new HttpEntity<Object>(), Void)

        then:
        1 * requestFactory.createRequest({ URI it ->
            it.toString().contains("group_urlname=${MEETUP_NAME_URL}")
        } as URI, HttpMethod.GET) >> { URI uri, HttpMethod method ->
            ClientHttpRequest request = new MockClientHttpRequest(method, uri)
            request.setResponse(new MockClientHttpResponse("{}".bytes, HttpStatus.OK))
            return request
        }
    }
}
