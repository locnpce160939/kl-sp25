package com.ftcs.rateandreview.repository;

import com.ftcs.rateandreview.model.Review;
import com.ftcs.rateandreview.projection.DriverReviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findReviewByReviewId(Integer reviewId);
//    Optional<Review> findReviewByPeopleRating(String peopleRating);
    @Query("SELECT r FROM Review r JOIN TripBookings t ON r.bookingId = t.bookingId WHERE t.accountId = :accountId")
    Page<Review> findAllByAccountId(@Param("accountId") Integer accountId, Pageable pageable);

    @Query(value = """
        EXEC [dbo].[GetDriverReviews] @DriverId = :driverId
        """, nativeQuery = true)
    Page<DriverReviewProjection> getDriverReviews(@Param("driverId") Integer driverId, Pageable pageable);
}
