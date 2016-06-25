package pl.jug.torun.xenia.meetup

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Created by krzysztof on 26.06.16.
 */
@Component
@ConfigurationProperties(prefix = "meetup")
class MeetupProperty {
    String key= ''
    String code= ''
    String groupId
    String token
    String groupUrlName


}
