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
@Table(name = "authors")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "Author.getByName",
                query = "select a from Author a where a.name = :name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
})
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "Author name cannot be blank.")
    private String name;



    @ManyToMany(mappedBy = "authors")
    List<Book> books;
}
