package pl.jug.torun.xenia.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import pl.jug.torun.xenia.Application
import pl.jug.torun.xenia.dao.PrizeRepository
import pl.jug.torun.xenia.model.Prize
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by DeX on 2016-05-14.
 */
@ContextConfiguration(loader = SpringApplicationContextLoader, classes = Application)
@IntegrationTest
class PrizeAutocompleteControllerSpec extends Specification {

    @Autowired
    PrizeRepository prizeRepository

    @Autowired
    PrizeAutocompleteController controller

    @Shared
    private boolean initialized

    def setup() {
        if (!initialized) {
            prizeRepository.deleteAll()
            prizeRepository.save(new Prize(name: "Licencja IntelliJ IDEA od JetBrains", producer: "JetBrains", sponsorName: "Zbyszko"))
            prizeRepository.save(new Prize(name: "Licencja jProfiler", producer: "ej-technologies GmbH", sponsorName: "Zbyszko"))
            prizeRepository.save(new Prize(name: "Licencja Retrospective", producer: "", sponsorName: "UMK"))
            prizeRepository.save(new Prize(name: "Miesięczna subskrypcja Pluralsight", producer: "Plurasight"))
            prizeRepository.save(new Prize(name: "Spring Boot in Action", producer: "Manning", sponsorName: "Toruń JUG"))
            initialized = true
        }
    }

    @Unroll
    def "should return #expected names for #name"() {
        when:
        def names = controller.getName(name)

        then:
        names == expected.toSet()

        where:
        name            | expected
        ""              | ["Licencja IntelliJ IDEA od JetBrains", "Licencja jProfiler", "Licencja Retrospective", "Miesięczna subskrypcja Pluralsight", "Spring Boot in Action"]
        "LIC"           | ["Licencja IntelliJ IDEA od JetBrains", "Licencja jProfiler", "Licencja Retrospective"]
        "jProfiler"     | []
    }


    @Unroll
    def "should return #expected sponsors for #name"() {
        when:
        def names = controller.getSponsor(name)

        then:
        names == expected.toSet()

        where:
        name            | expected
        ""              | ["Zbyszko", "UMK", "Toruń JUG"]
        "z"             | ["Zbyszko"]
        "dupa"          | []
        "um"            | ["UMK"]
        "umk "          | []
    }
}
