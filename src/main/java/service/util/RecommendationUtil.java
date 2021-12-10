package service.util;

import repository.RatingRepository;
import repository.UserRepository;
import repository.entity.Book;
import repository.entity.Rating;
import repository.entity.User;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class RecommendationUtil {
    @EJB
    private UserRepository userRepository;
    @EJB
    private RatingRepository ratingRepository;

    private static final int BIG_DECIMAL_SCALE = 32;

    private Map<User, Map<Book, BigDecimal>> reloadUserRatings() {
        Map<User, Map<Book, BigDecimal>> userRatings = new HashMap<>();

        // Get List of all users and convert it to Map
        List<User> userList = userRepository.getAllUsers();
        Map<String, User> userMap = userList.stream()
                .collect(Collectors.toMap(User::getUsername, user -> user));

        for(User user: userMap.values()) {
            // Get List of all ratings of each user and convert it to Map
            List<Rating> ratingList = ratingRepository.getAllRatingsOfUser(user);
            Map<Book, BigDecimal> ratingMap = ratingList.stream()
                    .collect(Collectors.toMap(Rating::getBook, Rating::getValue));

            userRatings.put(user, ratingMap);
        }

        return userRatings;
    }

    private BigDecimal getBookPreferenceSimilarityScore(final Map<User, Map<Book, BigDecimal>> userRatings,
                                                        final User user1, final User user2) {

        final Map<Book, BigDecimal> bookRatingsOfUser1 = userRatings.get(user1);
        final Map<Book, BigDecimal> bookRatingsOfUser2 = userRatings.get(user2);

        final List<Book> commonRatedBooks = new ArrayList<>();
        for(Book book: bookRatingsOfUser1.keySet()) {
            if(bookRatingsOfUser2.containsKey(book))
                commonRatedBooks.add(book);
        }

        BigDecimal sumOfSquares = new BigDecimal("0");
        for(final Book book: commonRatedBooks) {
            final BigDecimal rating1 = bookRatingsOfUser1.get(book);
            final BigDecimal rating2 = bookRatingsOfUser2.get(book);
            final BigDecimal ratingDiff = rating1.subtract(rating2);            // rating1 - rating2
            final BigDecimal ratingDiffSqr = ratingDiff.multiply(ratingDiff);   // (rating1 - rating2)^2
            sumOfSquares = sumOfSquares.add(ratingDiffSqr);                     // sumOfSquares += (rating1 - rating2)^2
        }

        final BigDecimal one = new BigDecimal("1");
        return one.divide(one.add(sumOfSquares), BIG_DECIMAL_SCALE, RoundingMode.HALF_UP);  // 1 / (1 + sumOfSquares)
    }


    public Map<Book, BigDecimal> getBookRecommendations(final User user) {
        Map<User, Map<Book, BigDecimal>> userRatings = reloadUserRatings();

        final Map<Book, BigDecimal> totals = new HashMap<>();
        final Map<Book, BigDecimal> similaritySums = new HashMap<>();

        for(final User other: userRatings.keySet()) {
            // Do not compare user to himself
            if(Objects.equals(other, user))
                continue;

            final BigDecimal similarity = getBookPreferenceSimilarityScore(userRatings, user, other);
            final BigDecimal zero = new BigDecimal("0");
            if(similarity.compareTo(zero) <= 0)  // if similarity <= 0
                continue;

            for(final Book book: userRatings.get(other).keySet()) {
                // Score only books user has not seen yet
                if(!userRatings.get(user).containsKey(book)) {
                    // Init totals[book] if it has not been
                    if(!totals.containsKey(book))
                        totals.put(book, zero);

                    final BigDecimal ratingOfOther = userRatings.get(other).get(book);
                    // totals[book] += ratingOfOther * similarity
                    totals.put(book, totals.get(book).add(ratingOfOther.multiply(similarity)));

                    // Init similaritySums[book] if it has not been
                    if(!similaritySums.containsKey(book))
                        similaritySums.put(book, zero);

                    // similaritySums[book] += similarity
                    similaritySums.put(book, similaritySums.get(book).add(similarity));
                }
            }
        }

        final Map<Book, BigDecimal> rankings = new HashMap<>();
        for(final Book book: totals.keySet()) {
            final BigDecimal total = totals.get(book);
            rankings.put(book, total.divide(similaritySums.get(book), BIG_DECIMAL_SCALE, RoundingMode.HALF_UP));
        }

        // Sort rankings in descending order
        return rankings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, HashMap::new));
    }
}
