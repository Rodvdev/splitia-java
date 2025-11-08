package com.splitia.controller;

import com.splitia.dto.request.CreateProductRequest;
import com.splitia.dto.request.CreateStockMovementRequest;
import com.splitia.dto.request.UpdateProductRequest;
import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.ProductResponse;
import com.splitia.dto.response.StockMovementResponse;
import com.splitia.dto.response.StockResponse;
import com.splitia.model.enums.ProductType;
import com.splitia.service.ProductService;
import com.splitia.service.StockAlertService;
import com.splitia.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management endpoints")
public class InventoryController {
    
    private final ProductService productService;
    private final StockService stockService;
    private final StockAlertService stockAlertService;
    
    // Products
    @GetMapping("/products")
    @Operation(summary = "Get all products with filters")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(
            Pageable pageable,
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        Page<ProductResponse> products = productService.getAllProducts(pageable, type, category, search);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/products/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @GetMapping("/products/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(@PathVariable String sku) {
        ProductResponse product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(product));
    }
    
    @PostMapping("/products")
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }
    
    @PutMapping("/products/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success(product, "Product updated successfully"));
    }
    
    @DeleteMapping("/products/{id}")
    @Operation(summary = "Delete product. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        productService.deleteProduct(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Product permanently deleted" : "Product soft deleted"));
    }
    
    // Stock
    @GetMapping("/stock/{productId}")
    @Operation(summary = "Get stock information for a product")
    public ResponseEntity<ApiResponse<StockResponse>> getStockByProductId(@PathVariable UUID productId) {
        StockResponse stock = stockService.getStockByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(stock));
    }
    
    @GetMapping("/stock/low-stock")
    @Operation(summary = "Get all products with low stock")
    public ResponseEntity<ApiResponse<List<StockResponse>>> getLowStock() {
        List<StockResponse> lowStock = stockAlertService.checkLowStock();
        return ResponseEntity.ok(ApiResponse.success(lowStock));
    }
    
    // Stock Movements
    @GetMapping("/movements")
    @Operation(summary = "Get all stock movements")
    public ResponseEntity<ApiResponse<Page<StockMovementResponse>>> getStockMovements(
            Pageable pageable,
            @RequestParam(required = false) UUID productId) {
        Page<StockMovementResponse> movements = stockService.getStockMovements(pageable, productId);
        return ResponseEntity.ok(ApiResponse.success(movements));
    }
    
    @PostMapping("/movements")
    @Operation(summary = "Create a new stock movement")
    public ResponseEntity<ApiResponse<StockMovementResponse>> createStockMovement(
            @Valid @RequestBody CreateStockMovementRequest request) {
        StockMovementResponse movement = stockService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(movement, "Stock movement created successfully"));
    }
    
    @DeleteMapping("/movements/{id}")
    @Operation(summary = "Delete stock movement. Use ?hard=true for hard delete, default is soft delete")
    public ResponseEntity<ApiResponse<Void>> deleteStockMovement(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean hard) {
        stockService.deleteStockMovement(id, hard);
        return ResponseEntity.ok(ApiResponse.success(null, hard ? "Stock movement permanently deleted" : "Stock movement soft deleted"));
    }
}

