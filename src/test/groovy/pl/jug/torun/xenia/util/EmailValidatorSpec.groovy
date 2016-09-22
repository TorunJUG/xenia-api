package pl.jug.torun.xenia.util

import spock.lang.Specification
import spock.lang.Unroll

class EmailValidatorSpec extends Specification {

    @Unroll
    def "Is e-mail #email valid? #isValid"() {
        expect:
        EmailValidator.isValid(email) == isValid

        where:
        email                               || isValid
        ""                                  || false
        null                                || false
        "a"                                 || false
        "a@a.com"                           || true
        "test+test@gmail.com"               || true
        "joe.doe@yahoo.com"                 || true
        "joe.doe"                           || false
        "joe.doe@"                          || false
        "@joe.doe"                          || false
        "joe.doe@gmail"                     || false
        "joe.doe@mywebsite.ninja"           || true
        "something@wp.pl"                   || true
        "jan.kowalski@o2.pl"                || true
        "jan.kowalski@gmail.com"            || true
        "jan.kowalski@op.pl"                || true
        "jan.kowalski@test.io"              || true
        "jan.kowalski@test.it"              || true
    }
}
