package service.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.RatingRepository;
import repository.UserRepository;
import repository.entity.Book;
import repository.entity.Rating;
import repository.entity.User;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;

class RecommendationUtilTest {
    @Mock
    UserRepository userRepository;
    @Mock
    RatingRepository ratingRepository;
    @InjectMocks
    RecommendationUtil recommendationUtil;

    static List<User> users;
    static Map<User, List<Rating>> userRatings;

    static User toby;
    static Book ladyInTheWater;
    static Book justMyLuck;
    static Book theNightListener;

    @BeforeAll
    static void init() {
        final User lisaRose = User.builder().username("Lisa Rose").build();
        final User geneSeymour = User.builder().username("Gene Seymour").build();
        final User michaelPhillips = User.builder().username("Michael Phillips").build();
        final User claudiaPuig = User.builder().username("Claudia Puig").build();
        final User mickLaSalle = User.builder().username("Mick LaSalle").build();
        final User jackMatthews = User.builder().username("Jack Matthews").build();
        toby = User.builder().username("Toby").build();

        users = Arrays.asList(lisaRose, geneSeymour, michaelPhillips, claudiaPuig, mickLaSalle, jackMatthews, toby);


        ladyInTheWater = Book.builder().title("Lady in the Water").build();
        final Book snakesOnAPlane = Book.builder().title("Snakes on a Plane").build();
        justMyLuck = Book.builder().title("Just My Luck").build();
        final Book supermanReturns = Book.builder().title("Superman Returns").build();
        final Book youMeAndDupree = Book.builder().title("You, Me and Dupree").build();
        theNightListener = Book.builder().title("The Night Listener").build();


        userRatings = new HashMap<>();

        final List<Rating> ratingsOfLisaRose = Arrays.asList(
                new Rating(1L, ladyInTheWater, lisaRose,  new BigDecimal("2.5")),
                new Rating(2L, snakesOnAPlane, lisaRose,  new BigDecimal("3.5")),
                new Rating(3L, justMyLuck, lisaRose,  new BigDecimal("3.0")),
                new Rating(4L, supermanReturns, lisaRose,  new BigDecimal("3.5")),
                new Rating(5L, youMeAndDupree, lisaRose,  new BigDecimal("2.5")),
                new Rating(6L, theNightListener, lisaRose,  new BigDecimal("3.0"))
        );
        userRatings.put(lisaRose, ratingsOfLisaRose);

        final List<Rating> ratingsOfGeneSeymour = Arrays.asList(
                new Rating(7L, ladyInTheWater, geneSeymour,  new BigDecimal("3.0")),
                new Rating(8L, snakesOnAPlane, geneSeymour,  new BigDecimal("3.5")),
                new Rating(9L, justMyLuck, geneSeymour,  new BigDecimal("1.5")),
                new Rating(10L, theNightListener, geneSeymour,  new BigDecimal("3.0")),
                new Rating(11L, youMeAndDupree, geneSeymour,  new BigDecimal("3.5"))
        );
        userRatings.put(geneSeymour, ratingsOfGeneSeymour);


        final List<Rating> ratingsOfMichaelPhillips = Arrays.asList(
                new Rating(12L, ladyInTheWater, michaelPhillips,  new BigDecimal("2.5")),
                new Rating(13L, snakesOnAPlane, michaelPhillips,  new BigDecimal("3.0")),
                new Rating(14L, supermanReturns, michaelPhillips,  new BigDecimal("3.5")),
                new Rating(15L, theNightListener, michaelPhillips,  new BigDecimal("4.0"))
        );
        userRatings.put(michaelPhillips, ratingsOfMichaelPhillips);

        final List<Rating> ratingsOfClaudiaPuig = Arrays.asList(
                new Rating(16L, snakesOnAPlane, claudiaPuig,  new BigDecimal("3.5")),
                new Rating(17L, justMyLuck, claudiaPuig,  new BigDecimal("3.0")),
                new Rating(18L, theNightListener, claudiaPuig,  new BigDecimal("4.5")),
                new Rating(19L, supermanReturns, claudiaPuig,  new BigDecimal("4.0")),
                new Rating(20L, youMeAndDupree, claudiaPuig,  new BigDecimal("2.5"))
        );
        userRatings.put(claudiaPuig, ratingsOfClaudiaPuig);

        final List<Rating> ratingsOfMickLaSalle = Arrays.asList(
                new Rating(21L, ladyInTheWater, mickLaSalle,  new BigDecimal("3.0")),
                new Rating(22L, snakesOnAPlane, mickLaSalle,  new BigDecimal("4.0")),
                new Rating(23L, justMyLuck, mickLaSalle,  new BigDecimal("2.0")),
                new Rating(24L, supermanReturns, mickLaSalle,  new BigDecimal("3.0")),
                new Rating(25L, theNightListener, mickLaSalle,  new BigDecimal("3.0")),
                new Rating(26L, youMeAndDupree, mickLaSalle,  new BigDecimal("2.0"))
        );
        userRatings.put(mickLaSalle, ratingsOfMickLaSalle);

        final List<Rating> ratingsOfJackMatthews = Arrays.asList(
                new Rating(27L, ladyInTheWater, jackMatthews,  new BigDecimal("3.0")),
                new Rating(28L, snakesOnAPlane, jackMatthews,  new BigDecimal("4.0")),
                new Rating(29L, justMyLuck, jackMatthews,  new BigDecimal("3.0")),
                new Rating(30L, supermanReturns, jackMatthews,  new BigDecimal("5.0")),
                new Rating(31L, theNightListener, jackMatthews,  new BigDecimal("3.5")),
                new Rating(32L, youMeAndDupree, jackMatthews,  new BigDecimal("3.5"))
        );
        userRatings.put(jackMatthews, ratingsOfJackMatthews);

        final List<Rating> ratingsOfToby = Arrays.asList(
                new Rating(33L, snakesOnAPlane, toby,  new BigDecimal("4.5")),
                new Rating(34L, youMeAndDupree, toby,  new BigDecimal("1.0")),
                new Rating(35L, supermanReturns, toby,  new BigDecimal("4.0"))
        );
        userRatings.put(toby, ratingsOfToby);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetBookRecommendations() {
        when(userRepository.getAllUsers()).thenReturn(users);
        for(User u: users) {
            when(ratingRepository.getAllRatingsOfUser(u)).thenReturn(userRatings.get(u));
        }

        final Map<Book, BigDecimal> expected = new HashMap<>();
        expected.put(theNightListener, new BigDecimal("3.5002478401415877"));
        expected.put(ladyInTheWater, new BigDecimal("2.7561242939959363"));
        expected.put(justMyLuck, new BigDecimal("2.4619884860743739"));

        final Map<Book, BigDecimal> result = recommendationUtil.getBookRecommendations(toby);

        for(Book book: result.keySet()) {
            BigDecimal expectedRating = expected.get(book);
            BigDecimal resultRating = result.get(book);
            Assertions.assertTrue(areBigDecimalsNearlyEqual(expectedRating, resultRating));
        }
    }

    private boolean areBigDecimalsNearlyEqual(BigDecimal a, BigDecimal b) {
        BigDecimal nonnegativeDiff = a.subtract(b).abs();
        BigDecimal nearlyZero = new BigDecimal("0.1");
        return nonnegativeDiff.compareTo(nearlyZero) <= 0;
    }
}
