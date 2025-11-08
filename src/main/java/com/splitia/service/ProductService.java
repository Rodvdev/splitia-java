package com.splitia.service;

import com.splitia.dto.request.CreateProductRequest;
import com.splitia.dto.request.CreateProductVariantRequest;
import com.splitia.dto.request.UpdateProductRequest;
import com.splitia.dto.response.ProductResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.ProductMapper;
import com.splitia.model.enums.ProductType;
import com.splitia.model.inventory.Product;
import com.splitia.model.inventory.ProductVariant;
import com.splitia.model.inventory.Stock;
import com.splitia.repository.*;
import com.splitia.service.websocket.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final StockRepository stockRepository;
    private final ProductMapper productMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable, ProductType type, String category, String search) {
        Specification<Product> spec = Specification.where(null);
        
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        
        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        
        if (search != null && !search.isEmpty()) {
            Specification<Product> searchSpec = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("sku")), "%" + search.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
            );
            spec = spec.and(searchSpec);
        }
        
        spec = spec.and((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        
        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toResponse(product);
    }
    
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return productMapper.toResponse(product);
    }
    
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        // Check if SKU already exists
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new BadRequestException("Product with SKU " + request.getSku() + " already exists");
        }
        
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setType(request.getType());
        product.setPrice(request.getPrice());
        product.setCost(request.getCost());
        product.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        product.setCategory(request.getCategory());
        product.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        
        product = productRepository.save(product);
        
        // Create stock
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(0);
        stock.setMinQuantity(request.getMinQuantity() != null ? request.getMinQuantity() : 0);
        stock.setMaxQuantity(request.getMaxQuantity());
        stock.setLocation(request.getLocation());
        stock = stockRepository.save(stock);
        product.setStock(stock);
        
        // Create variants if provided
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            List<ProductVariant> variants = new ArrayList<>();
            for (CreateProductVariantRequest variantRequest : request.getVariants()) {
                if (productVariantRepository.findBySku(variantRequest.getSku()).isPresent()) {
                    throw new BadRequestException("Variant with SKU " + variantRequest.getSku() + " already exists");
                }
                ProductVariant variant = new ProductVariant();
                variant.setSku(variantRequest.getSku());
                variant.setName(variantRequest.getName());
                variant.setPrice(variantRequest.getPrice());
                variant.setCost(variantRequest.getCost());
                variant.setAttributes(variantRequest.getAttributes());
                variant.setProduct(product);
                variants.add(variant);
            }
            product.setVariants(variants);
            product = productRepository.save(product);
        }
        
        ProductResponse response = productMapper.toResponse(product);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("product", response);
        webSocketNotificationService.notifyProductCreated(product.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        // Check SKU uniqueness if changed
        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.findBySku(request.getSku()).isPresent()) {
                throw new BadRequestException("Product with SKU " + request.getSku() + " already exists");
            }
            product.setSku(request.getSku());
        }
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getType() != null) product.setType(request.getType());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCost() != null) product.setCost(request.getCost());
        if (request.getCurrency() != null) product.setCurrency(request.getCurrency());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        if (request.getImages() != null) product.setImages(request.getImages());
        
        // Update stock if provided
        if (product.getStock() != null) {
            Stock stock = product.getStock();
            if (request.getMinQuantity() != null) stock.setMinQuantity(request.getMinQuantity());
            if (request.getMaxQuantity() != null) stock.setMaxQuantity(request.getMaxQuantity());
            if (request.getLocation() != null) stock.setLocation(request.getLocation());
            stockRepository.save(stock);
        }
        
        // Update variants if provided
        if (request.getVariants() != null) {
            product.getVariants().clear();
            for (CreateProductVariantRequest variantRequest : request.getVariants()) {
                ProductVariant variant = new ProductVariant();
                variant.setSku(variantRequest.getSku());
                variant.setName(variantRequest.getName());
                variant.setPrice(variantRequest.getPrice());
                variant.setCost(variantRequest.getCost());
                variant.setAttributes(variantRequest.getAttributes());
                variant.setProduct(product);
                product.getVariants().add(variant);
            }
        }
        
        product = productRepository.save(product);
        ProductResponse response = productMapper.toResponse(product);
        
        // Emit WebSocket event
        Map<String, Object> data = new HashMap<>();
        data.put("product", response);
        webSocketNotificationService.notifyProductUpdated(product.getId(), data, getCurrentUserId());
        
        return response;
    }
    
    @Transactional
    public void deleteProduct(UUID id, boolean hardDelete) {
        if (hardDelete) {
            productRepository.deleteById(id);
        } else {
            Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);
        }
    }
    
    private UUID getCurrentUserId() {
        try {
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof com.splitia.security.CustomUserDetails) {
                com.splitia.security.CustomUserDetails userDetails = 
                    (com.splitia.security.CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
        } catch (Exception e) {
            // If not authenticated, return null
        }
        return null;
    }
}

