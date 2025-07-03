package com.ecopreco.controller;

import com.ecopreco.dto.PriceHistoryResponse;
import com.ecopreco.dto.PriceStatisticsResponse;
import com.ecopreco.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/price-history")
@RequiredArgsConstructor
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PriceHistoryResponse>> getPriceHistoryByProductId(@PathVariable Long productId) {
        List<PriceHistoryResponse> history = priceHistoryService.getPriceHistoryByProductId(productId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/product/{productId}/statistics")
    public ResponseEntity<PriceStatisticsResponse> getPriceStatisticsByProductId(@PathVariable Long productId) {
        PriceStatisticsResponse statistics = priceHistoryService.getPriceStatisticsByProductId(productId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/product/{productId}/statistics/period")
    public ResponseEntity<PriceStatisticsResponse> getPriceStatisticsByProductIdAndPeriod(
            @PathVariable Long productId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        PriceStatisticsResponse statistics = priceHistoryService.getPriceStatisticsByProductIdAndPeriod(productId, start, end);
        return ResponseEntity.ok(statistics);
    }
}


