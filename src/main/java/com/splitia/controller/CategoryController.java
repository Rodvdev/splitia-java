package com.splitia.controller;

import com.splitia.dto.response.ApiResponse;
import com.splitia.dto.response.CategoryResponse;
import com.splitia.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @GetMapping
    @Operation(summary = "Get user categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getUserCategories() {
        List<CategoryResponse> categories = categoryService.getUserCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}

