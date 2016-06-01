package pl.jug.torun.xenia

import com.google.common.base.Predicate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import static com.google.common.base.Predicates.not
import static com.google.common.base.Predicates.or
import static springfox.documentation.builders.PathSelectors.regex

@SpringBootApplication
@EnableSwagger2
class Application {

    @Autowired
    ManagementServerProperties managementServerProperties

    static void main(String[] args) {
        SpringApplication.run Application, args
    }

    @Bean
    public Docket getDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
        .select().paths(getPaths()).build()
        .ignoredParameterTypes(MetaClass.class) // for groovy
    }


    private Predicate getPaths(){ //ignore endpoints from actuator and standard error endpoint
        return not(or(
                regex(managementServerProperties.contextPath+".*"),
                regex("/error.*")
        ))
    }
}
