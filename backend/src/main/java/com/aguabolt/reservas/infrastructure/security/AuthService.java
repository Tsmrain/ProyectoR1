package com.aguabolt.reservas.infrastructure.security;

import com.aguabolt.reservas.domain.models.EstadoHabitacion;
import com.aguabolt.reservas.domain.models.Habitacion;
import com.aguabolt.reservas.domain.models.Rol;
import com.aguabolt.reservas.domain.models.Usuario;
import com.aguabolt.reservas.infrastructure.repositories.HabitacionRepository;
import com.aguabolt.reservas.infrastructure.repositories.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final HabitacionRepository habitacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostConstruct
    public void init() {
        if (usuarioRepository.findByEmail("admin@residencial.com").isEmpty()) {
            Usuario admin = Usuario.builder()
                    .email("admin@residencial.com")
                    .password(passwordEncoder.encode("12345"))
                    .rol(Rol.ADMIN)
                    .build();
            usuarioRepository.save(admin);
        }

        if (habitacionRepository.findAll().isEmpty()) {
            Habitacion h1 = Habitacion.builder()
                    .numero("101")
                    .estado(EstadoHabitacion.DISPONIBLE)
                    .tarifa(new BigDecimal("50.00"))
                    .tiempoXLimpieza(30)
                    .build();
            habitacionRepository.save(h1);
        }
    }

    public String login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        return jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getRol().name());
    }
}
