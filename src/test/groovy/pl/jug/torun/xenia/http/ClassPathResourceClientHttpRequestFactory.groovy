package pl.jug.torun.xenia.http

import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.mock.http.client.MockClientHttpRequest
import org.springframework.mock.http.client.MockClientHttpResponse

class ClassPathResourceClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final String resourcePath
    private final String contentType

    ClassPathResourceClientHttpRequestFactory(String resourcePath, String contentType) {
        this.resourcePath = resourcePath
        this.contentType = contentType
    }

    @Override
    ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        ClassPathResource json = new ClassPathResource(resourcePath)
        MockClientHttpResponse response = new MockClientHttpResponse(json.inputStream, HttpStatus.OK)
        response.getHeaders().add("Content-Type", contentType)

        MockClientHttpRequest request = new MockClientHttpRequest(httpMethod, uri)
        request.setResponse(response)

        return request
    }
}
