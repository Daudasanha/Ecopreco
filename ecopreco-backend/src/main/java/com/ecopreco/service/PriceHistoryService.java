package com.ecopreco.service;

import com.ecopreco.dto.PriceHistoryResponse;
import com.ecopreco.dto.PriceStatisticsResponse;
import com.ecopreco.exception.ResourceNotFoundException;
import com.ecopreco.model.PriceHistory;
import com.ecopreco.model.Product;
import com.ecopreco.repository.PriceHistoryRepository;
import com.ecopreco.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<PriceHistoryResponse> getPriceHistoryByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + productId);
        }
        
        List<PriceHistory> histories = priceHistoryRepository.findByProductIdOrderByRecordedAtDesc(productId);
        return histories.stream()
                .map(this::mapPriceHistoryToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PriceStatisticsResponse getPriceStatisticsByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + productId));
        
        Object[] stats = priceHistoryRepository.findPriceStatisticsByProductId(productId);
        
        if (stats == null || stats.length == 0) {
            return new PriceStatisticsResponse(
                    product.getCurrentPrice(),
                    product.getCurrentPrice(),
                    product.getCurrentPrice(),
                    productId,
                    product.getName()
            );
        }
        
        BigDecimal minPrice = (BigDecimal) stats[0];
        BigDecimal maxPrice = (BigDecimal) stats[1];
        BigDecimal avgPrice = (BigDecimal) stats[2];
        
        return new PriceStatisticsResponse(minPrice, maxPrice, avgPrice, productId, product.getName());
    }

    @Transactional(readOnly = true)
    public PriceStatisticsResponse getPriceStatisticsByProductIdAndPeriod(Long productId, LocalDateTime startDate, LocalDateTime endDate) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + productId));
        
        Object[] stats = priceHistoryRepository.findPriceStatisticsByProductIdAndDateRange(productId, startDate, endDate);
        
        if (stats == null || stats.length == 0) {
            return new PriceStatisticsResponse(
                    product.getCurrentPrice(),
                    product.getCurrentPrice(),
                    product.getCurrentPrice(),
                    productId,
                    product.getName()
            );
        }
        
        BigDecimal minPrice = (BigDecimal) stats[0];
        BigDecimal maxPrice = (BigDecimal) stats[1];
        BigDecimal avgPrice = (BigDecimal) stats[2];
        
        return new PriceStatisticsResponse(minPrice, maxPrice, avgPrice, productId, product.getName());
    }

    private PriceHistoryResponse mapPriceHistoryToResponse(PriceHistory priceHistory) {
        return new PriceHistoryResponse(
                priceHistory.getId(),
                priceHistory.getProduct().getId(),
                priceHistory.getProduct().getName(),
                priceHistory.getPrice(),
                priceHistory.getStore(),
                priceHistory.getRecordedAt()
        );
    }
}

