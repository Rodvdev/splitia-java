package com.splitia.service;

import com.splitia.dto.request.CreateCategoryRequest;
import com.splitia.dto.request.UpdateCategoryRequest;
import com.splitia.dto.response.CategoryResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ForbiddenException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.mapper.CategoryMapper;
import com.splitia.model.CustomCategory;
import com.splitia.model.Group;
import com.splitia.model.User;
import com.splitia.repository.CategoryRepository;
import com.splitia.repository.GroupRepository;
import com.splitia.repository.GroupUserRepository;
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
    private final GroupRepository groupRepository;
    private final GroupUserRepository groupUserRepository;
    private final CategoryMapper categoryMapper;
    
    public Page<CategoryResponse> getCategories(UUID groupId, Pageable pageable) {
        User currentUser = getCurrentUser();
        
        // Verify user is member of the group
        if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), groupId)) {
            throw new ForbiddenException("You are not a member of this group");
        }
        
        return categoryRepository.findByGroupId(groupId, pageable)
                .map(categoryMapper::toResponse);
    }
    
    public CategoryResponse getCategoryById(UUID categoryId) {
        User currentUser = getCurrentUser();
        CustomCategory category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Verify user is member of the group
        if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), category.getGroup().getId())) {
            throw new ForbiddenException("You are not a member of this group");
        }
        
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        User currentUser = getCurrentUser();
        
        // Verify user is member of the group
        if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), request.getGroupId())) {
            throw new ForbiddenException("You are not a member of this group");
        }
        
        // Get group
        Group group = groupRepository.findByIdAndDeletedAtIsNull(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId()));
        
        // Check if category with same name already exists in this group
        if (categoryRepository.existsByGroupIdAndName(request.getGroupId(), request.getName())) {
            throw new BadRequestException("Category with this name already exists in this group");
        }
        
        CustomCategory category = new CustomCategory();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setColor(request.getColor());
        category.setGroup(group);
        category.setCreatedBy(currentUser);
        
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }
    
    @Transactional
    public CategoryResponse updateCategory(UUID categoryId, UpdateCategoryRequest request) {
        User currentUser = getCurrentUser();
        CustomCategory category = categoryRepository.findByIdAndDeletedAtIsNull(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        // Verify user is member of the group
        if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), category.getGroup().getId())) {
            throw new ForbiddenException("You are not a member of this group");
        }
        
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            // Check if new name already exists in this group
            if (categoryRepository.existsByGroupIdAndName(category.getGroup().getId(), request.getName())) {
                throw new BadRequestException("Category with this name already exists in this group");
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
        
        // Verify user is member of the group
        if (!groupUserRepository.existsByUserIdAndGroupId(currentUser.getId(), category.getGroup().getId())) {
            throw new ForbiddenException("You are not a member of this group");
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

