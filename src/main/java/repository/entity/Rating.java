package repository.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import controller.util.jsonformatting.BigDecimalSerializer;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "ratings")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
        @NamedQuery(name = "Rating.getByBookAndUser",
                query = "select r from Rating r where r.book = :book and r.user = :user",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
})
public class Rating implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Book cannot be null in Rating.")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null in Rating.")
    private User user;

    @Column(name = "value", nullable = false)
    @NotNull(message = "Rating value cannot be null.")
    @Digits(integer = 1, fraction = 1)
    @DecimalMin(value = "0.0", message = "Rating value cannot be less than 0.")
    @DecimalMax(value = "5.0", message = "Rating value cannot be greater than 5.")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal value;
}
