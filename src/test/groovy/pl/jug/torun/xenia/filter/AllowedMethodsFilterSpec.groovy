package pl.jug.torun.xenia.filter

import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AllowedMethodsFilterSpec extends Specification {

    private final HttpServletRequest request = new MockHttpServletRequest()

    private final HttpServletResponse response = new MockHttpServletResponse()

    private final FilterChain filterChain = new MockFilterChain()

    @Subject
    private AllowedMethodsFilter filter = new AllowedMethodsFilter()


    def "should set Access-Control-Allow-Origin header"() {
        when:
        filter.doFilter(request, response, filterChain)

        then:
        response.getHeader("Access-Control-Allow-Origin") == "*"
    }

    def "should set Access-Control-Allow-Methods header"() {
        when:
        filter.doFilter(request, response, filterChain)

        then:
        response.getHeader("Access-Control-Allow-Methods") == "POST, GET, PUT, OPTIONS, DELETE, PATCH"
    }

    def "should set Access-Control-Max-Age header"() {
        when:
        filter.doFilter(request, response, filterChain)

        then:
        response.getHeader("Access-Control-Max-Age") == "3600"
    }

    def "should set Access-Control-Allow-Headers header"() {
        when:
        filter.doFilter(request, response, filterChain)

        then:
        response.getHeader("Access-Control-Allow-Headers") == "x-requested-with, Content-Type, Accept"
    }
}
