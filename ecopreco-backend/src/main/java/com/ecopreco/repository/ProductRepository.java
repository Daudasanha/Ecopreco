package com.ecopreco.repository;

import com.ecopreco.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Busca por palavra-chave em múltiplos campos
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.store) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.ecoLabel) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.sustainabilityInfo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Busca por categoria
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);
    
    // Busca por marca
    Page<Product> findByBrandIgnoreCase(String brand, Pageable pageable);
    
    // Busca por loja
    Page<Product> findByStoreIgnoreCase(String store, Pageable pageable);
    
    // Busca por faixa de preço
    Page<Product> findByCurrentPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Busca por selo ecológico
    Page<Product> findByEcoLabelIgnoreCase(String ecoLabel, Pageable pageable);
    
    // Busca produtos com selo ecológico (não nulo)
    Page<Product> findByEcoLabelIsNotNull(Pageable pageable);
    
    // Busca por avaliação mínima
    Page<Product> findByAverageRatingGreaterThanEqual(BigDecimal minRating, Pageable pageable);
    
    // Busca combinada com filtros
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR LOWER(p.category) = LOWER(:category)) AND " +
           "(:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) AND " +
           "(:store IS NULL OR LOWER(p.store) = LOWER(:store)) AND " +
           "(:minPrice IS NULL OR p.currentPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.currentPrice <= :maxPrice) AND " +
           "(:ecoLabel IS NULL OR LOWER(p.ecoLabel) = LOWER(:ecoLabel)) AND " +
           "(:minRating IS NULL OR p.averageRating >= :minRating)")
    Page<Product> findWithFilters(@Param("category") String category,
                                  @Param("brand") String brand,
                                  @Param("store") String store,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("ecoLabel") String ecoLabel,
                                  @Param("minRating") BigDecimal minRating,
                                  Pageable pageable);
    
    // Busca todas as categorias distintas
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findDistinctCategories();
    
    // Busca todas as marcas distintas
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findDistinctBrands();
    
    // Busca todas as lojas distintas
    @Query("SELECT DISTINCT p.store FROM Product p WHERE p.store IS NOT NULL ORDER BY p.store")
    List<String> findDistinctStores();
    
    // Busca todos os selos ecológicos distintos
    @Query("SELECT DISTINCT p.ecoLabel FROM Product p WHERE p.ecoLabel IS NOT NULL ORDER BY p.ecoLabel")
    List<String> findDistinctEcoLabels();
}

