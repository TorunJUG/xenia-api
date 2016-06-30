package pl.jug.torun.xenia

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = Application)
@ContextConfiguration(classes = MockServletContext)
@WebAppConfiguration
class ApplicationTests {

    @Test
    void contextLoads() {}
}
