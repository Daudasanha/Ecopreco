package com.ecopreco.controller;

import com.ecopreco.dto.MessageResponse;
import com.ecopreco.dto.ReviewRequest;
import com.ecopreco.dto.ReviewResponse;
import com.ecopreco.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole(\'USER\') or hasRole(\'ADMIN\')")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest reviewRequest, Principal principal) {
        ReviewResponse newReview = reviewService.createReview(reviewRequest, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(\'USER\') or hasRole(\'ADMIN\')")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id, @Valid @RequestBody ReviewRequest reviewRequest, Principal principal) {
        ReviewResponse updatedReview = reviewService.updateReview(id, reviewRequest, principal.getName());
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(\'USER\') or hasRole(\'ADMIN\')")
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long id, Principal principal) {
        reviewService.deleteReview(id, principal.getName());
        return ResponseEntity.ok(new MessageResponse("Avaliação deletada com sucesso!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable Long id) {
        ReviewResponse review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<ReviewResponse> reviews = reviewService.getReviewsByProduct(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole(\'USER\') or hasRole(\'ADMIN\')")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<ReviewResponse> reviews = reviewService.getReviewsByUser(username, pageable);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<BigDecimal> getAverageRatingByProduct(@PathVariable Long productId) {
        BigDecimal averageRating = reviewService.getAverageRatingByProduct(productId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> getReviewCountByProduct(@PathVariable Long productId) {
        Long reviewCount = reviewService.getReviewCountByProduct(productId);
        return ResponseEntity.ok(reviewCount);
    }

    @GetMapping("/product/{productId}/rating-distribution")
    public ResponseEntity<List<Object[]>> getRatingDistributionByProduct(@PathVariable Long productId) {
        List<Object[]> distribution = reviewService.getRatingDistributionByProduct(productId);
        return ResponseEntity.ok(distribution);
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<ReviewResponse>> getRecentReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<ReviewResponse> reviews = reviewService.getRecentReviews(pageable);
        return ResponseEntity.ok(reviews);
    }
}


