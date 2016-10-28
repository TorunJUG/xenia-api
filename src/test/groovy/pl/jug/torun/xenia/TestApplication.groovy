package pl.jug.torun.xenia

import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import pl.jug.torun.xenia.meetup.MeetupRestTemplate

@Configuration
@CompileStatic
@Import(Application)
class TestApplication {

    @Bean
    public MeetupRestTemplate meetupRestTemplate() {
        return new MeetupRestTemplate(UUID.randomUUID().toString(), "Meetup-API-Testing")
    }
}
