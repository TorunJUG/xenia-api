package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@WebAppConfiguration
@IntegrationTest
class PrizeControllerSpec extends Specification {

    @Autowired
    WebApplicationContext webApplicationContext

    @Autowired
    PrizeRepository prizeRepository

    MockMvc request

    void setup() {
        if (request == null) {
            request = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    .dispatchOptions(true)
                    .build()
        }
    }

    def "Should not allow to add product with name that is in used"() {
        given:
            def json = '{ "name": "Test", "producer": "Microsoft" }'
        when:
            def response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content(json))
        then:
            def createdPrizeId = getCreatedPrizeId()
            response.andExpect(status().isCreated())
                    .andExpect(jsonPath('$.resourceUrl', is(equalTo("/prize/" + createdPrizeId))))
        when:
            response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content(json))
        then:
            response.andExpect(status().isBadRequest())
                    .andExpect(jsonPath('$.message', is(equalTo("Prize with name 'Test' already exists"))))
        when:
            response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content('{ "name": "Test2", "producer": "Microsoft" }'))
        then:
            response.andExpect(status().isCreated())
                    .andExpect(jsonPath('$.resourceUrl', is(equalTo('/prize/' + (createdPrizeId + 1)))))
    }

    private long getCreatedPrizeId() {
        List<Prize> allPrizes = prizeRepository.findAll()
        return allPrizes ? allPrizes.last().id : 1
    }

    def "Should allow updating existing prizes"() {
        given:
            def prize = prizeRepository.save(new Prize(name: 'updateTest', producer: 'Microsoft'))
            def json = '{"name":"updateTestUpdated"}'
        when:
            def response = request.perform(put("/prize/${prize.id}").contentType(MediaType.APPLICATION_JSON).content(json))
        then:
            response.andExpect(status().isOk())
                    .andExpect(jsonPath('$.name', is(equalTo('updateTestUpdated'))))
                    .andExpect(jsonPath('$.id', is(equalTo(prize.id as int))))
                    .andExpect(jsonPath('$.producer', is(equalTo('Microsoft'))))
    }

    def "Should not allow to use name that is alredy used"() {
        given:
            prizeRepository.save(new Prize(name: 'prize2', producer: 'Microsoft'))

            def prize = prizeRepository.save(new Prize(name: 'prize', producer: 'Microsoft'))
            def prizeJson = '{"name":"prize2"}'
        when:
            def response = request.perform(put("/prize/${prize.id}").contentType(MediaType.APPLICATION_JSON).content(prizeJson))
        then:
            response.andExpect(status().isBadRequest())
    }

    def "Should allow use the same name while updating producer"() {
        given:
            def prize = prizeRepository.save(new Prize(name: 'prize4', producer: 'Microsoft'))
            def json = '{"name":"prize4","producer":"Google"}'
        when:
            def response = request.perform(put("/prize/${prize.id}").contentType(MediaType.APPLICATION_JSON).content(json))
        then:
            response.andExpect(status().isOk())
                    .andExpect(jsonPath('$.name', is(equalTo('prize4'))))
                    .andExpect(jsonPath('$.id', is(equalTo(prize.id as int))))
                    .andExpect(jsonPath('$.producer', is(equalTo('Google'))))
    }
}
