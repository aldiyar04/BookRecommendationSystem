package repository.entity;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "books")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "Book.getByIsbn",
                query = "select b from Book b where b.isbn = :isbn",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}),
})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    @NotBlank(message = "Book title cannot be blank.")
    private String title;

    @Column(name = "description", length = 4096, nullable = false)
    @NotBlank(message = "Book description cannot be blank.")
    private String description;

    @Column(name = "isbn", nullable = false, unique = true)
    @NotBlank(message = "Book ISBN cannot be blank.")
    private String isbn;

    @Column(name = "year_published", nullable = false)
    @NotNull(message = "Book publication year cannot be null.")
    @Min(value = 1000, message = "Book publication year cannot be less than 1000.")
    @Max(value = 2050, message = "Book publication year cannot be greater than 2050.")
    private Short yearPublished;



    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @JsonbTransient
    List<Author> authors = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @JsonbTransient
    List<Genre> genres = new ArrayList<>();
}