package com.aguabolt.reservas.controllers;

import com.aguabolt.reservas.domain.models.Reserva;
import com.aguabolt.reservas.domain.services.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @Test
    void testCrearReservaEndpoint() throws Exception {
        Reserva mockReserva = Reserva.builder()
                .id(1L)
                .nombreCliente("Carlos Cliente")
                .build();

        when(reservaService.crearReserva(anyLong(), anyString(), any(LocalDateTime.class)))
                .thenReturn(mockReserva);

        mockMvc.perform(post("/api/reservas")
                .param("habitacionId", "1")
                .param("nombreCliente", "Carlos Cliente")
                .param("horaInicio", "2026-04-17T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCliente").value("Carlos Cliente"));
    }
}
