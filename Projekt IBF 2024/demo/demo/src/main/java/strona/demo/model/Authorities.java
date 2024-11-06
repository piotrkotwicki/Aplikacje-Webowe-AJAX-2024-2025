package strona.demo.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class Authorities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String authority;
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Users user;
}