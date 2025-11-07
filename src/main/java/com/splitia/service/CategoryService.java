package com.splitia.service;

import com.splitia.dto.response.CategoryResponse;
import com.splitia.mapper.CategoryMapper;
import com.splitia.model.CustomCategory;
import com.splitia.model.User;
import com.splitia.repository.CategoryRepository;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;
    
    public List<CategoryResponse> getUserCategories() {
        User currentUser = getCurrentUser();
        return categoryRepository.findByUserId(currentUser.getId())
                .stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

