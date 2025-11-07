package com.splitia.security;

import com.splitia.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private User user;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole() != null ? user.getRole() : "USER";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getEmail();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public UUID getUserId() {
        return user.getId();
    }
}

