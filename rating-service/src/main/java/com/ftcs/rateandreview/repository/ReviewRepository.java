package com.ftcs.rateandreview.repository;

import com.ftcs.rateandreview.model.Review;
import com.ftcs.rateandreview.projection.DriverReviewProjection;
import com.ftcs.rateandreview.projection.MainStatisticsProjection;
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
        SELECT a.FullName, r.Rating, r.ReviewText, r.CreateAt, r.UpdateAt
        FROM Review r
        JOIN TripBookings t ON r.BookingId = t.BookingId
        JOIN Account a ON t.AccountId = a.AccountId
        JOIN TripAgreement ta ON t.TripAgreementId = ta.Id
        WHERE ta.DriverId = :driverId
        ORDER BY r.CreateAt DESC
        """, nativeQuery = true)
    Page<DriverReviewProjection> getDriverReviews(@Param("driverId") Integer driverId, Pageable pageable);

    boolean existsByBookingId(Long bookingId);

    @Query(value = "EXEC dbo.sp_GetMainStatistics", nativeQuery = true)
    List<MainStatisticsProjection> getMainStatistics();
}
