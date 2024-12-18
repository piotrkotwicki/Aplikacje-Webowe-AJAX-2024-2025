package strona.demo.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Authorities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String authority;
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    private Users user;
}