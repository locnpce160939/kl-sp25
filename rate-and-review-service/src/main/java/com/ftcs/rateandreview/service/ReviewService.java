package com.ftcs.rateandreview.service;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.rateandreview.dto.ReviewRequestDTO;
import com.ftcs.rateandreview.model.Review;
import com.ftcs.rateandreview.repository.ReviewRepository;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final TripBookingsRepository tripBookingsRepository;
    private final AccountRepository accountRepository;

    public void createRateAndReview(Integer accountId, ReviewRequestDTO requestDTO, Integer bookingId, String role) {
        TripBookings tripBookings = getTripBookingsByBookingId(bookingId);
        validateReviewRequest(requestDTO);
        validateOwnership(accountId, tripBookings);
        if ("Order completed".equalsIgnoreCase(tripBookings.getStatus())) {
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

    public List<Review> findAllReviewsByAccountId(Integer accountId) {
        return reviewRepository.findAllByAccountId(accountId);
    }

    private void validateOwnership(Integer accountId, TripBookings tripBookings) {
        if (!Objects.equals(tripBookings.getAccountId(), accountId)) {
            throw new BadRequestException("Yo u are not authorized to perform this action.");
        }
    }

    private void validateReviewRequest(ReviewRequestDTO requestDTO) {
        if (requestDTO.getReviewText() != null && !requestDTO.getReviewText().isEmpty()
                && requestDTO.getRating() == null) {
            throw new IllegalArgumentException("Rating cannot be null when review text is provided.");
        }
    }

    private TripBookings getTripBookingsByBookingId(Integer bookingId) {
        return tripBookingsRepository.findTripBookingsByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Trip booking not found."));
    }

    private Review getReviewById(Integer reviewId) {
        return reviewRepository.findReviewByReviewId(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found."));
    }
}
