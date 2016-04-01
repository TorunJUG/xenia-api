package pl.jug.torun.xenia

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.mock.web.MockServletContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = Application)
@ContextConfiguration(classes = MockServletContext)
@WebAppConfiguration
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
