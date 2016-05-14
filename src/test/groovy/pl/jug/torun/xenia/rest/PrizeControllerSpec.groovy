package pl.jug.torun.xenia.rest

import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.MediaType
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.dao.GiveAwayRepository
import pl.jug.torun.xenia.model.Prize
import pl.jug.torun.xenia.model.GiveAway
import spock.lang.Stepwise

import static org.hamcrest.Matchers.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.assertj.core.api.Assertions.assertThat

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@WebAppConfiguration
@IntegrationTest
class PrizeControllerSpec  {

    @Autowired
    protected WebApplicationContext webApplicationContext

    protected MockMvc request
    @Autowired
    protected PrizeRepository prizeRepository
    @Autowired
    protected GiveAwayRepository giveAwayRepository
    
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
    void shouldAllowUpdatingExistingPrizes(){
        //given:
        Prize prize = prizeRepository.save(new Prize(name: 'updateTest',producer:'Microsoft'))
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
    void shouldNotAllowToUseNameThatIsAlredyUsed(){
        //given:
        Prize prize1 = prizeRepository.save(new Prize(name: 'prize1',producer:'Microsoft'))
        Prize prize2 = prizeRepository.save(new Prize(name: 'prize2',producer:'Microsoft'))
        String json = '{"name":"prize2"}'
        
        //when:
        def response = request.perform(put("/prize/${prize1.id}").contentType(MediaType.APPLICATION_JSON).content(json))
        
        //then:
        response.andExpect(status().isBadRequest())
    } 
    
    @Test
    void shouldAllowUseTheSameNameWhileUpdatingProducer(){
        //given:
        Prize prize = prizeRepository.save(new Prize(name: 'prize4',producer:'Microsoft'))
      
        String json = '{"name":"prize4","producer":"Google"}'
        
        //when:
        def response = request.perform(put("/prize/${prize.id}").contentType(MediaType.APPLICATION_JSON).content(json))
        
        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.name', is(equalTo('prize4'))))
                .andExpect(jsonPath('$.id', is(equalTo(prize.id as int))))
                .andExpect(jsonPath('$.producer', is(equalTo('Google'))))
    } 
    
    @Test
    void shouldAllowDeletingPrize(){
        //given: 
         Prize prize = prizeRepository.save(new Prize(name: 'prizeDELETE',producer:'Microsoft'))
         
        //when: 
        def response = request.perform(delete("/prize/${prize.id}"));
        
        //then: 
        response.andExpect(status().isOk())
        long count = prizeRepository.countByName('prizeDELETE')
        assertThat(count).isEqualTo(0 as long)
    }
    
    @Test
    void shouldNotAllowDeletingUsedPrize(){
        //given: 
        Prize prize = prizeRepository.save(new Prize(name: 'prizeDELETE2',producer:'Microsoft'))
        GiveAway giveaway = giveAwayRepository.save(new GiveAway(prize: prize, amount: 10))
        
        //when:
        def response = request.perform(delete("/prize/${prize.id}"));
        
        //then:
        response.andExpect(status().isBadRequest())
    }
}
