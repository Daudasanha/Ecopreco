package com.ecopreco.service;

import com.ecopreco.dto.ReviewRequest;
import com.ecopreco.dto.ReviewResponse;
import com.ecopreco.exception.BadRequestException;
import com.ecopreco.exception.ResourceNotFoundException;
import com.ecopreco.model.Product;
import com.ecopreco.model.Review;
import com.ecopreco.model.User;
import com.ecopreco.repository.ProductRepository;
import com.ecopreco.repository.ReviewRepository;
import com.ecopreco.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public ReviewResponse createReview(ReviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProductId()));
        
        // Verifica se o usuário já avaliou este produto
        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), user.getId())) {
            throw new BadRequestException("Você já avaliou este produto");
        }

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        
        // Atualiza a média de avaliações do produto
        productService.updateProductRating(request.getProductId());

        return mapReviewToResponse(review);
    }

    public ReviewResponse updateReview(Long id, ReviewRequest request, String username) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        
        // Verifica se o usuário é o dono da avaliação
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Você só pode editar suas próprias avaliações");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);
        
        // Atualiza a média de avaliações do produto
        productService.updateProductRating(review.getProduct().getId());

        return mapReviewToResponse(review);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));
        return mapReviewToResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + productId);
        }
        
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(this::mapReviewToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::mapReviewToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByProductAndRating(Long productId, Integer rating, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + productId);
        }
        
        return reviewRepository.findByProductIdAndRatingOrderByCreatedAtDesc(productId, rating, pageable)
                .map(this::mapReviewToResponse);
    }

    @Transactional(readOnly = true)
    public BigDecimal getAverageRatingByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + productId);
        }
        
        BigDecimal average = reviewRepository.findAverageRatingByProductId(productId);
        return average != null ? average : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public Long getReviewCountByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + productId);
        }
        
        return reviewRepository.countByProductId(productId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getRatingDistributionByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + productId);
        }
        
        return reviewRepository.countByProductIdGroupByRating(productId);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getRecentReviews(Pageable pageable) {
        return reviewRepository.findRecentReviews(pageable)
                .map(this::mapReviewToResponse);
    }

    public void deleteReview(Long id, String username) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada com ID: " + id));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        
        // Verifica se o usuário é o dono da avaliação ou é admin
        if (!review.getUser().getId().equals(user.getId()) && 
            !user.getRoles().contains(com.ecopreco.model.Role.ROLE_ADMIN)) {
            throw new BadRequestException("Você só pode deletar suas próprias avaliações");
        }

        Long productId = review.getProduct().getId();
        reviewRepository.deleteById(id);
        
        // Atualiza a média de avaliações do produto
        productService.updateProductRating(productId);
    }

    private ReviewResponse mapReviewToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getProduct().getName(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}

