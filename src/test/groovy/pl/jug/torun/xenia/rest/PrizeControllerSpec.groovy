package pl.jug.torun.xenia.rest

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@WebIntegrationTest(randomPort = true)
class PrizeControllerSpec {

    @Autowired
    protected WebApplicationContext webApplicationContext

    protected MockMvc request
    @Autowired
    protected PrizeRepository prizeRepository

    @Before
    void setup() {
        if (request == null) {
            request = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    .dispatchOptions(true)
                    .build()
        }
    }

    @Test
    void shouldNotAllowToAddProductWithNameThatIsInUsed() {
        //given:
        String json = '''
{ "name": "Test", "producer": "Microsoft" }
'''

        //when:
        def response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content(json))

        //then:
        Long createdPrizeId = getCreatedPrizeId()
        response.andExpect(status().isCreated())
                .andExpect(jsonPath('$.resourceUrl', is(equalTo("/prize/" + createdPrizeId))))

        //when:
        response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content(json))

        //then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.message', is(equalTo("Prize with name 'Test' already exists"))))

        //when:
        response = request.perform(post("/prize").contentType(MediaType.APPLICATION_JSON).content('{ "name": "Test2", "producer": "Microsoft" }'))

        //then:
        response.andExpect(status().isCreated())
                .andExpect(jsonPath('$.resourceUrl', is(equalTo('/prize/' + (createdPrizeId + 1)))))
    }

    private long getCreatedPrizeId() {
        List<Prize> allPrizes = prizeRepository.findAll()
        return allPrizes ? allPrizes.last().id : 1
    }

    @Test
    void shouldAllowUpdatingExistingPrizes() {
        //given:
        Prize prize = prizeRepository.save(new Prize(name: 'updateTest', producer: 'Microsoft'))
        String json = '{"name":"updateTestUpdated"}'

        //when:
        def response = request.perform(put("/prize/${prize.id}").contentType(MediaType.APPLICATION_JSON).content(json))

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(equalTo('updateTestUpdated'))))
                .andExpect(jsonPath('$.id', is(equalTo(prize.id as int))))
                .andExpect(jsonPath('$.producer', is(equalTo('Microsoft'))))
    }

    @Test
    void shouldNotAllowToUseNameThatIsAlredyUsed() {
        //given:
        Prize prize1 = prizeRepository.save(new Prize(name: 'prize1', producer: 'Microsoft'))
        Prize prize2 = prizeRepository.save(new Prize(name: 'prize2', producer: 'Microsoft'))
        String json = '{"name":"prize2"}'

        //when:
        def response = request.perform(put("/prize/${prize1.id}").contentType(MediaType.APPLICATION_JSON).content(json))

        //then:
        response.andExpect(status().isBadRequest())
    }

    @Test
    void shouldAllowUseTheSameNameWhileUpdatingProducer() {
        //given:
        Prize prize = prizeRepository.save(new Prize(name: 'prize4', producer: 'Microsoft'))

        String json = '{"name":"prize4","producer":"Google"}'

        //when:
        def response = request.perform(put("/prize/${prize.id}").contentType(MediaType.APPLICATION_JSON).content(json))

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(equalTo('prize4'))))
                .andExpect(jsonPath('$.id', is(equalTo(prize.id as int))))
                .andExpect(jsonPath('$.producer', is(equalTo('Google'))))
    }
}
