package pl.jug.torun.xenia.prizes

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.ColumnDefault
import org.hibernate.validator.constraints.NotBlank

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@EqualsAndHashCode
@ToString(includePackage = false, includeFields = true)
final class Prize {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id

    @NotBlank
    String name

    String imageUrl

    @ColumnDefault("false")
    boolean inactive

    private Prize() {}

    Prize(String name, String imageUrl, boolean inactive) {
        this.name = name
        this.imageUrl = imageUrl
        this.inactive = inactive
    }
}
