package pl.jug.torun.xenia

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.jug.torun.xenia.meetup.MeetupRestTemplate

@Configuration
@ConfigurationProperties(prefix = "meetup")
class MeetupConfig {

    String key
    String groupUrlName

    @Bean
    public MeetupRestTemplate meetupRestTemplate() {
        return new MeetupRestTemplate(key, groupUrlName)
    }
}
