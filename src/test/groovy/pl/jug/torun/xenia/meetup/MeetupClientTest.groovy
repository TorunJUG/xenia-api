package pl.jug.torun.xenia.meetup

import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests
import pl.jug.torun.xenia.Application

@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@IntegrationTest
class MeetupClientTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    MeetupClient meetupClient

    @Before
    void setup() {
        meetupClient.key = 'xxx'
    }

    @Test
    void test() {

        meetupClient.findAllEvents()
    }

}
