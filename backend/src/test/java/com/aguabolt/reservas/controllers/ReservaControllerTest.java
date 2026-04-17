package com.aguabolt.reservas.controllers;

import com.aguabolt.reservas.domain.models.Reserva;
import com.aguabolt.reservas.domain.models.Usuario;
import com.aguabolt.reservas.domain.services.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(ReservaController.class)
@AutoConfigureMockMvc
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private com.aguabolt.reservas.infrastructure.security.JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(username = "carlos@test.com", roles = "CLIENTE")
    void testCrearReservaEndpoint() throws Exception {
        Usuario cliente = Usuario.builder().email("carlos@test.com").build();
        Reserva mockReserva = Reserva.builder()
                .id(1L)
                .cliente(cliente)
                .build();

        when(reservaService.crearReserva(anyLong(), anyString(), any(LocalDateTime.class)))
                .thenReturn(mockReserva);

        mockMvc.perform(post("/api/reservas")
                .with(csrf())
                .param("habitacionId", "1")
                .param("horaInicio", "2026-04-17T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.email").value("carlos@test.com"));
    }
}
