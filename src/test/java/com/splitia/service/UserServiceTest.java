package com.splitia.service;

import com.splitia.dto.request.RegisterRequest;
import com.splitia.exception.BadRequestException;
import com.splitia.mapper.UserMapper;
import com.splitia.model.User;
import com.splitia.repository.UserRepository;
import com.splitia.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtTokenProvider tokenProvider;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;
    
    private RegisterRequest registerRequest;
    
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@example.com");
        registerRequest.setPassword("password123");
    }
    
    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(java.util.UUID.randomUUID());
            return user;
        });
        
        // When & Then
        assertDoesNotThrow(() -> userService.register(registerRequest));
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        
        // When & Then
        assertThrows(BadRequestException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}

