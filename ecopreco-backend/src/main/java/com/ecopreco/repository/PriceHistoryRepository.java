package com.ecopreco.repository;

import com.ecopreco.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    
    // Busca histórico de preços por produto
    List<PriceHistory> findByProductIdOrderByRecordedAtDesc(Long productId);
    
    // Busca histórico de preços por produto e loja
    List<PriceHistory> findByProductIdAndStoreIgnoreCaseOrderByRecordedAtDesc(Long productId, String store);
    
    // Busca histórico de preços por produto em um período
    List<PriceHistory> findByProductIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            Long productId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Busca o preço mais recente de um produto
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.product.id = :productId " +
           "ORDER BY ph.recordedAt DESC LIMIT 1")
    PriceHistory findLatestByProductId(@Param("productId") Long productId);
    
    // Busca o preço mais recente de um produto em uma loja específica
    @Query("SELECT ph FROM PriceHistory ph WHERE ph.product.id = :productId " +
           "AND LOWER(ph.store) = LOWER(:store) ORDER BY ph.recordedAt DESC LIMIT 1")
    PriceHistory findLatestByProductIdAndStore(@Param("productId") Long productId, @Param("store") String store);
    
    // Estatísticas de preço por produto
    @Query("SELECT MIN(ph.price), MAX(ph.price), AVG(ph.price) FROM PriceHistory ph " +
           "WHERE ph.product.id = :productId")
    Object[] findPriceStatisticsByProductId(@Param("productId") Long productId);
    
    // Estatísticas de preço por produto em um período
    @Query("SELECT MIN(ph.price), MAX(ph.price), AVG(ph.price) FROM PriceHistory ph " +
           "WHERE ph.product.id = :productId AND ph.recordedAt BETWEEN :startDate AND :endDate")
    Object[] findPriceStatisticsByProductIdAndDateRange(@Param("productId") Long productId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);
}

