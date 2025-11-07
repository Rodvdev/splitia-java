package com.splitia.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Wrapper para BCryptPasswordEncoder
 * En producción, considerar usar un bean configurado con fuerza específica
 */
@Component
public class PasswordEncoderUtil {
    
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
    
    public PasswordEncoder getEncoder() {
        return encoder;
    }
}

