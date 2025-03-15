package com.ftcs.rateandreview.controller;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.rateandreview.ReviewURL;
import com.ftcs.rateandreview.dto.ReviewRequestDTO;
import com.ftcs.rateandreview.model.Review;
import com.ftcs.rateandreview.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ReviewURL.REVIEW)
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER') or hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createReview(@Valid @RequestBody ReviewRequestDTO requestDTO,
                                       @PathVariable("bookingId") Long bookingId,
                                       @RequestAttribute("role" ) String role,
                                       @RequestAttribute("accountId") Integer accountId) {
        reviewService.createRateAndReview(accountId, requestDTO, bookingId, role);
        return new ApiResponse<>("Review created");
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<?> getAllReviewsByAccountId(@PathVariable("accountId") Integer accountId,
                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Review> reviews = reviewService.findAllReviewsByAccountId(accountId, page, size);
        return new ApiResponse<>("Fetched reviews successfully.", reviews);
    }

    @GetMapping("/driver/{driverId}")
    public ApiResponse<?> getAllReviewsDriver(@PathVariable("driverId") Integer driverId,
                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>("Get reviews driver successfully.", reviewService.findAllReviewsDriver(driverId, page, size));
    }

    @PutMapping("updateReview/{reviewId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER') or hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateReview(@Valid @RequestBody ReviewRequestDTO requestDTO,
                                       @PathVariable("reviewId") Integer reviewId,
                                       @RequestAttribute("accountId") Integer accountId) {
        reviewService.updateReview(accountId, requestDTO, reviewId);
        return new ApiResponse<>("Review updated");
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER') or hasPermission(null, 'DRIVER')")
    public ApiResponse<?> deleteReview(@PathVariable("reviewId") Integer reviewId, @RequestAttribute("accountId") Integer accountId) {
        reviewService.deleteReview(accountId, reviewId);
        return new ApiResponse<>("Review deleted");
    }

}
