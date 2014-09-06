package pl.jug.torun.xenia.meetup

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.stereotype.Service

@Service
class MeetupClient {

    private static final String MEETUP_API_HOST = 'https://api.meetup.com'
    private static final String FORMAT = 'json'

    String key;

    List findAllEvents() {
        HttpClient client = HttpClientBuilder.create().build()
        HttpGet request = new HttpGet(getEndpointUrl('/2/events', [key: key]))

        //HttpResponse response = client.execute(request)


        return null
    }

    private static String getEndpointUrl(String endpoint, Map params = [:]) {


        return MEETUP_API_HOST + endpoint + '.' + FORMAT + '?' + (params.inject('') { queryString, k, v -> queryString += "$k=$v&" })
    }
}
