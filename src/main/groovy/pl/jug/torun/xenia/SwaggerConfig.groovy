package pl.jug.torun.xenia

import com.google.common.base.Predicate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import static com.google.common.base.Predicates.not
import static com.google.common.base.Predicates.or
import static springfox.documentation.builders.PathSelectors.regex

/**
 * Created by krzysztof on 25.06.16.
 */
@ConditionalOnWebApplication
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Autowired
    ManagementServerProperties managementServerProperties;

    @Bean
    public Docket getDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select().paths(getPaths()).build()
                .ignoredParameterTypes(MetaClass.class) // for groovy
    }


    private Predicate getPaths() { //ignore endpoints from actuator and standard error endpoint
        return not(or(
                regex(managementServerProperties.getContextPath() + ".*"),
                regex("/error.*")
        ))
    }
}