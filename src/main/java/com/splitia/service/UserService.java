package com.splitia.service;

import com.splitia.dto.request.ChangePasswordRequest;
import com.splitia.dto.request.LoginRequest;
import com.splitia.dto.request.RegisterRequest;
import com.splitia.dto.request.UpdatePreferencesRequest;
import com.splitia.dto.request.UpdateProfileRequest;
import com.splitia.dto.response.AuthResponse;
import com.splitia.dto.response.UserResponse;
import com.splitia.exception.BadRequestException;
import com.splitia.exception.ResourceNotFoundException;
import com.splitia.exception.UnauthorizedException;
import com.splitia.mapper.UserMapper;
import com.splitia.model.User;
import com.splitia.repository.UserRepository;
import com.splitia.security.CustomUserDetails;
import com.splitia.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        
        user = userRepository.save(user);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setUser(userMapper.toResponse(user));
        
        return response;
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            String token = tokenProvider.generateToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken(authentication);
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
            
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setRefreshToken(refreshToken);
            response.setUser(userMapper.toResponse(user));
            
            return response;
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }
    
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
        
        return userMapper.toResponse(user);
    }
    
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUserEntity();
        
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getImage() != null) {
            user.setImage(request.getImage());
        }
        
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
    
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUserEntity();
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    @Transactional
    public UserResponse updatePreferences(UpdatePreferencesRequest request) {
        User user = getCurrentUserEntity();
        
        if (request.getCurrency() != null) {
            user.setCurrency(request.getCurrency());
        }
        if (request.getLanguage() != null) {
            user.setLanguage(request.getLanguage());
        }
        if (request.getTheme() != null) {
            user.setTheme(request.getTheme());
        }
        if (request.getNotificationsEnabled() != null) {
            user.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        if (request.getDateFormat() != null) {
            user.setDateFormat(request.getDateFormat());
        }
        if (request.getTimeFormat() != null) {
            user.setTimeFormat(request.getTimeFormat());
        }
        
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
    
    private User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));
    }
}

