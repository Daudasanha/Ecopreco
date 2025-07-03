package com.ecopreco.service;

import com.ecopreco.dto.ProductRequest;
import com.ecopreco.dto.ProductResponse;
import com.ecopreco.exception.ResourceNotFoundException;
import com.ecopreco.model.Product;
import com.ecopreco.repository.ProductRepository;
import com.ecopreco.repository.ReviewRepository;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        mapRequestToProduct(request, product);
        product = productRepository.save(product);
        return mapProductToResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        
        mapRequestToProduct(request, product);
        product = productRepository.save(product);
        return mapProductToResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        return mapProductToResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByKeyword(keyword, pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsWithFilters(String category, String brand, String store,
                                                       BigDecimal minPrice, BigDecimal maxPrice,
                                                       String ecoLabel, BigDecimal minRating,
                                                       Pageable pageable) {
        return productRepository.findWithFilters(category, brand, store, minPrice, maxPrice, ecoLabel, minRating, pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryIgnoreCase(category, pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandIgnoreCase(brand, pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByStore(String store, Pageable pageable) {
        return productRepository.findByStoreIgnoreCase(store, pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByCurrentPriceBetween(minPrice, maxPrice, pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByEcoLabel(String ecoLabel, Pageable pageable) {
        return productRepository.findByEcoLabelIgnoreCase(ecoLabel, pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getSustainableProducts(Pageable pageable) {
        return productRepository.findByEcoLabelIsNotNull(pageable)
                .map(this::mapProductToResponse);
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctCategories() {
        return productRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctBrands() {
        return productRepository.findDistinctBrands();
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctStores() {
        return productRepository.findDistinctStores();
    }

    @Transactional(readOnly = true)
    public List<String> getDistinctEcoLabels() {
        return productRepository.findDistinctEcoLabels();
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com ID: " + id);
        }
        productRepository.deleteById(id);
    }

    public void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + productId));
        
        BigDecimal averageRating = reviewRepository.findAverageRatingByProductId(productId);
        product.setAverageRating(averageRating);
        productRepository.save(product);
    }

    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCurrentPrice(request.getCurrentPrice());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setEcoLabel(request.getEcoLabel());
        product.setProductUrl(request.getProductUrl());
        product.setImageUrl(request.getImageUrl());
        product.setStore(request.getStore());
        product.setSustainabilityInfo(request.getSustainabilityInfo());
    }

    private ProductResponse mapProductToResponse(Product product) {
        Long reviewCount = reviewRepository.countByProductId(product.getId());
        
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCurrentPrice(),
                product.getBrand(),
                product.getCategory(),
                product.getEcoLabel(),
                product.getProductUrl(),
                product.getImageUrl(),
                product.getStore(),
                product.getSustainabilityInfo(),
                product.getAverageRating(),
                reviewCount,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}

