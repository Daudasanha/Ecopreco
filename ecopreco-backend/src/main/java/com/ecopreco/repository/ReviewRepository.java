package com.ecopreco.repository;

import com.ecopreco.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Busca avaliações por produto
    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    // Busca avaliações por usuário
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Busca avaliações por produto e usuário
    List<Review> findByProductIdAndUserId(Long productId, Long userId);
    
    // Busca avaliações por rating
    Page<Review> findByProductIdAndRatingOrderByCreatedAtDesc(Long productId, Integer rating, Pageable pageable);
    
    // Calcula a média de avaliações de um produto
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    BigDecimal findAverageRatingByProductId(@Param("productId") Long productId);
    
    // Conta o número de avaliações de um produto
    Long countByProductId(Long productId);
    
    // Conta avaliações por rating de um produto
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> countByProductIdGroupByRating(@Param("productId") Long productId);
    
    // Busca avaliações mais recentes
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findRecentReviews(Pageable pageable);
    
    // Verifica se usuário já avaliou um produto
    Boolean existsByProductIdAndUserId(Long productId, Long userId);
}

