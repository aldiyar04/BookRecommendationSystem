package repository.entity;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "users")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "User.getByUsername",
                query = "select u from User u where u.username = :username",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}),
        @NamedQuery(name = "User.getByEmail",
                query = "select u from User u where u.email = :email",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false)
    private Long id;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Email must be valid.")
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    @NotBlank(message = "Username cannot be blank.")
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "salt", nullable = false)
    private String salt;



    public static final String ROLE_USER = "user";
    public static final String ROLE_LIBRARIAN = "librarian";
    public static final String ROLE_ADMIN = "admin";
}
