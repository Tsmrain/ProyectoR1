package com.aguabolt.reservas.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
    }

    @Test
    void testGenerateAndValidateToken() {
        String email = "test@example.com";
        String role = "ADMIN";
        
        String token = jwtTokenProvider.generateToken(email, role);
        
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token));
    }

    @Test
    void testRejectInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    void testRejectModifiedToken() {
        String token = jwtTokenProvider.generateToken("test@example.com", "ADMIN");
        String modifiedToken = token + "modified";
        assertFalse(jwtTokenProvider.validateToken(modifiedToken));
    }
}
