package com.ftcs.rateandreview.repository;

import com.ftcs.rateandreview.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findReviewByReviewId(Integer reviewId);
//    Optional<Review> findReviewByPeopleRating(String peopleRating);
    @Query("SELECT r FROM Review r JOIN TripBookings t ON r.bookingId = t.bookingId WHERE t.accountId = :accountId")
    List<Review> findAllByAccountId(@Param("accountId") Integer accountId);
}
