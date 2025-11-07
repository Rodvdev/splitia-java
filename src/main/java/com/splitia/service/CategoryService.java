package com.splitia.service;

import com.splitia.dto.request.CreateCategoryRequest;
import com.splitia.dto.request.UpdateCategoryRequest;
import com.splitia.dto.response.CategoryResponse;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.CategoryMapper;
import com.splitia.model.CustomCategory;
import com.splitia.model.User;
import com.splitia.repository.CategoryRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    
    public Page<CategoryResponse> getCategories(Pageable pageable) {
        User currentUser = getCurrentUser();
        return categoryRepository.findByUserId(currentUser.getId(), pageable)
                .map(categoryMapper::toResponse);
    }
    
    public CategoryResponse getCategoryById(UUID categoryId) {
        User currentUser = getCurrentUser();
        CustomCategory category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Verify ownership
        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        User currentUser = getCurrentUser();
        
        // Check if category with same name already exists
        if (categoryRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
            throw new RuntimeException("Category with this name already exists");
        }
        
        CustomCategory category = new CustomCategory();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setUser(currentUser);
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest request) {
        User currentUser = getCurrentUser();
        CustomCategory category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Verify ownership
        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            // Check if new name already exists
            if (categoryRepository.existsByUserIdAndName(currentUser.getId(), request.getName())) {
                throw new RuntimeException("Category with this name already exists");
            }
            category.setName(request.getName());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            category.setColor(request.getColor());
        }
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public void softDeleteCategory(UUID categoryId) {
        User currentUser = getCurrentUser();
        CustomCategory category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Verify ownership
        if (!category.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findByIdAndDeletedAtIsNull(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

