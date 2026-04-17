package com.aguabolt.reservas.domain.services;

import com.aguabolt.reservas.domain.models.EstadoHabitacion;
import com.aguabolt.reservas.domain.models.EstadoReserva;
import com.aguabolt.reservas.domain.models.Habitacion;
import com.aguabolt.reservas.domain.models.Reserva;
import com.aguabolt.reservas.domain.models.Usuario;
import com.aguabolt.reservas.infrastructure.repositories.HabitacionRepository;
import com.aguabolt.reservas.infrastructure.repositories.ReservaRepository;
import com.aguabolt.reservas.infrastructure.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void testCrearReserva_HabitacionDisponible() {
        Habitacion h = Habitacion.builder().id(1L).numero("101").estado(EstadoHabitacion.DISPONIBLE).build();
        Usuario u = Usuario.builder().email("test@test.com").build();
        LocalDateTime inicio = LocalDateTime.now();
        
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(h));
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(u));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArguments()[0]);

        Reserva res = reservaService.crearReserva(1L, "test@test.com", inicio);

        assertThat(res.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(res.getTiempoLimiteCheckIn()).isEqualTo(inicio.plusMinutes(30));
    }

    @Test
    void testCrearReserva_HabitacionOcupada() {
        Habitacion h = Habitacion.builder().id(1L).numero("101").estado(EstadoHabitacion.OCUPADA).build();
        Usuario u = Usuario.builder().email("test@test.com").build();
        
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(h));
        when(usuarioRepository.findByEmail("test@test.com")).thenReturn(Optional.of(u));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArguments()[0]);

        Reserva res = reservaService.crearReserva(1L, "test@test.com", LocalDateTime.now());

        assertThat(res.getEstado()).isEqualTo(EstadoReserva.EN_ESPERA);
        assertThat(res.getTiempoLimiteCheckIn()).isNull();
    }

    @Test
    void testFinalizarLimpieza_ConColaDeEspera() {
        Habitacion h = Habitacion.builder().id(1L).numero("101").estado(EstadoHabitacion.LIMPIEZA).build();
        Reserva rEspera = Reserva.builder().id(10L).estado(EstadoReserva.EN_ESPERA).habitacion(h).build();

        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(h));
        when(reservaRepository.findFirstByHabitacionIdAndEstadoOrderByCreatedAtAsc(1L, EstadoReserva.EN_ESPERA))
                .thenReturn(Optional.of(rEspera));
        when(habitacionRepository.save(any(Habitacion.class))).thenAnswer(i -> i.getArguments()[0]);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(i -> i.getArguments()[0]);

        Habitacion result = reservaService.finalizarLimpieza(1L);

        assertThat(result.getEstado()).isEqualTo(EstadoHabitacion.DISPONIBLE);
        assertThat(rEspera.getEstado()).isEqualTo(EstadoReserva.CONFIRMADA);
        assertThat(rEspera.getTiempoLimiteCheckIn()).isNotNull();
    }

    @Test
    void testProcesarNoShowsAutomatico() {
        Reserva v = Reserva.builder().id(100L).estado(EstadoReserva.CONFIRMADA).habitacion(new Habitacion()).build();
        
        when(reservaRepository.findByEstadoAndTiempoLimiteCheckInBefore(eq(EstadoReserva.CONFIRMADA), any(LocalDateTime.class)))
                .thenReturn(List.of(v));
        when(reservaRepository.findFirstByHabitacionIdAndEstadoOrderByCreatedAtAsc(any(), any()))
                .thenReturn(Optional.empty());

        reservaService.procesarNoShowsAutomatico();

        assertThat(v.getEstado()).isEqualTo(EstadoReserva.CANCELADA_POR_NO_SHOW);
        verify(reservaRepository, atLeastOnce()).save(v);
    }
}
