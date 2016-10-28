package pl.jug.torun.xenia.meetup

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import pl.jug.torun.xenia.util.EmailValidator

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Meetup member class.
 */
@Entity
@JsonDeserialize(using = Deserializer)
@EqualsAndHashCode
@ToString(includePackage = false, includeNames = true)
final class Member {
    @Id
    long id
    String name
    String photoUrl
    String email

    private Member() {}

    public Member(long id, String name, String photoUrl, String email) {
        this.id = id
        this.name = name
        this.photoUrl = photoUrl
        this.email = email
    }

    static class Deserializer extends JsonDeserializer<Member> {
        @Override
        Member deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);

            return new Member(
                    id: node.at("/member/member_id").longValue(),
                    name: node.at("/member/name").textValue(),
                    photoUrl: node.at("/member_photo/thumb_link").textValue() ?: "",
                    email: node.at("/answers").collect { it.textValue() }.find { EmailValidator.isValid(it) } ?: ""
            )
        }
    }
}
