package repository.entity;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "genres")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "Genre.getByName",
                query = "select g from Genre g where g.name = :name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
})
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "Genre name cannot be blank.")
    private String name;



    @ManyToMany(mappedBy = "genres")
    List<Book> books;
}