package com.aguabolt.reservas.infrastructure.security;

import com.aguabolt.reservas.domain.models.Rol;
import com.aguabolt.reservas.domain.models.Usuario;
import com.aguabolt.reservas.infrastructure.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private Usuario testUser;

    @BeforeEach
    void setUp() {
        testUser = Usuario.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .rol(Rol.ADMIN)
                .build();
    }

    @Test
    void testLoginSuccess() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken("test@example.com", "ADMIN")).thenReturn("fake-jwt-token");

        String token = authService.login("test@example.com", "password123");

        assertEquals("fake-jwt-token", token);
    }

    @Test
    void testLoginInvalidPassword() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login("test@example.com", "wrongPassword");
        });
    }

    @Test
    void testLoginUserNotFound() {
        when(usuarioRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login("nonexistent@example.com", "anyPassword");
        });
    }
}
