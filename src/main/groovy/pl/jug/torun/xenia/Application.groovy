package pl.jug.torun.xenia

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import org.joda.time.DateTime
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(basePackages = ["pl.jug.torun.xenia"])
@SpringBootApplication
class Application {

    @Bean
    public ObjectMapper objectMapper() {
        Module module = new SimpleModule()
        module.addSerializer(DateTime, new JsonSerializer<DateTime>() {
            @Override
            void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                String dateString = dateTime.toString()
                jsonGenerator.writeString(dateString)
            }
        })

        ObjectMapper mapper = new ObjectMapper()
        mapper.registerModule(module)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper
    }

    static void main(String[] args) {
        SpringApplication.run Application, args
    }
}
