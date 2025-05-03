package com.ftcs.rateandreview.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.exception.UnauthorizedException;
import com.ftcs.rateandreview.dto.MainStatisticsDTO;
import com.ftcs.rateandreview.dto.ReviewRequestDTO;
import com.ftcs.rateandreview.model.Review;
import com.ftcs.rateandreview.projection.DriverReviewProjection;
import com.ftcs.rateandreview.projection.MainStatisticsProjection;
import com.ftcs.rateandreview.repository.ReviewRepository;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TripBookingsRepository tripBookingsRepository;

    public void createRateAndReview(Integer accountId, ReviewRequestDTO requestDTO, Long bookingId, String role) {
        TripBookings tripBookings = getTripBookingsByBookingId(bookingId);
        isExistingReview(bookingId);
        validateReviewRequest(requestDTO);
        validateOwnership(accountId, tripBookings);
        if (TripBookingStatus.ORDER_COMPLETED == tripBookings.getStatus()) {
            Review review = new Review();
            review.setBookingId(bookingId);
            review.setPeopleRating(role);
            review.setRating(requestDTO.getRating());
            review.setReviewText(requestDTO.getReviewText());
            reviewRepository.save(review);
        } else {
            throw new BadRequestException("Trip booking must be completed to leave a review.");
        }
    }

    public void updateReview(Integer accountId, ReviewRequestDTO requestDTO, Integer reviewId) {
        Review existingReview = getReviewById(reviewId);
        TripBookings tripBookings = getTripBookingsByBookingId(existingReview.getBookingId());
        validateReviewRequest(requestDTO);
        validateOwnership(accountId, tripBookings);
        existingReview.setReviewText(requestDTO.getReviewText());
        existingReview.setRating(requestDTO.getRating());
        reviewRepository.save(existingReview);
    }

    public void deleteReview(Integer accountId, Integer reviewId) {
        Review review = getReviewById(reviewId);
        TripBookings tripBookings = getTripBookingsByBookingId(review.getBookingId());
        validateOwnership(accountId, tripBookings);
        reviewRepository.delete(review);
    }

    public Page<Review> findAllReviewsByAccountId(Integer accountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findAllByAccountId(accountId, pageable);
    }

    public Page<DriverReviewProjection> findAllReviewsDriver(Integer driverId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.getDriverReviews(driverId, pageable);
    }

    public List<MainStatisticsDTO> getMainStatistics() {
        return reviewRepository.getMainStatistics().stream()
                .map(stat -> new MainStatisticsDTO(
                        stat.getCategory(),
                        stat.getMetric(),
                        stat.getValue()))
                .collect(Collectors.toList());
    }

    private void validateOwnership(Integer accountId, TripBookings tripBookings) {
        if (!Objects.equals(tripBookings.getAccountId(), accountId)) {
            throw new UnauthorizedException("You are not authorized to perform this action.");
        }
    }

    private void validateReviewRequest(ReviewRequestDTO requestDTO) {
        if (requestDTO.getReviewText() != null && !requestDTO.getReviewText().isEmpty()
                && requestDTO.getRating() == null) {
            throw new BadRequestException("Rating cannot be null when review text is provided.");
        }
    }

    private void isExistingReview(Long bookingId) {
        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new BadRequestException("A review already exists for this booking.");
        }
    }

    private TripBookings getTripBookingsByBookingId(Long bookingId) {
        return tripBookingsRepository.findTripBookingsByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("Trip booking not found."));
    }

    private Review getReviewById(Integer reviewId) {
        return reviewRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found."));
    }
}
