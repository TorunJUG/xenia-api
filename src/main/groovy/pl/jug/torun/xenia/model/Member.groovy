package pl.jug.torun.xenia.model



import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Created by mephi_000 on 06.09.14.
 */
@Entity
class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id
    @Column(nullable = false)
    String displayName
    @Column(nullable = true)
    String photoUrl

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", id=" + id +
                '}';
    }
}
