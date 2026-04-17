package com.aguabolt.reservas.infrastructure.repositories;

import com.aguabolt.reservas.domain.models.EstadoHabitacion;
import com.aguabolt.reservas.domain.models.EstadoReserva;
import com.aguabolt.reservas.domain.models.Habitacion;
import com.aguabolt.reservas.domain.models.Reserva;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Test
    void testFindFirstByHabitacionIdAndEstadoOrderByCreatedAtAsc() {
        Habitacion h = habitacionRepository.save(Habitacion.builder()
                .numero("101")
                .estado(EstadoHabitacion.OCUPADA)
                .tarifa(java.math.BigDecimal.valueOf(50.0))
                .tiempoXLimpieza(30)
                .build());
        
        reservaRepository.save(Reserva.builder()
                .nombreCliente("Antiguo")
                .estado(EstadoReserva.EN_ESPERA)
                .habitacion(h)
                .horaInicio(LocalDateTime.now())
                .build());
        
        // Simular un pequeño retraso no es posible tan fácil sin cambiar elCreatedAt manual si JPA lo maneja, 
        // pero save secuencial debería asignar IDs/fechas incrementales.
        
        reservaRepository.save(Reserva.builder()
                .nombreCliente("Nuevo")
                .estado(EstadoReserva.EN_ESPERA)
                .habitacion(h)
                .horaInicio(LocalDateTime.now())
                .build());

        Optional<Reserva> result = reservaRepository.findFirstByHabitacionIdAndEstadoOrderByCreatedAtAsc(h.getId(), EstadoReserva.EN_ESPERA);
        
        assertThat(result).isPresent();
        assertThat(result.get().getNombreCliente()).isEqualTo("Antiguo");
    }

    @Test
    void testFindByEstadoAndTiempoLimiteCheckInBefore() {
        Habitacion h = habitacionRepository.save(Habitacion.builder()
                .numero("102")
                .estado(EstadoHabitacion.DISPONIBLE)
                .tarifa(java.math.BigDecimal.valueOf(70.0))
                .tiempoXLimpieza(30)
                .build());
        LocalDateTime ahora = LocalDateTime.now();

        // Reserva vencida (hace 1 hora)
        reservaRepository.save(Reserva.builder()
                .nombreCliente("Vencido")
                .estado(EstadoReserva.CONFIRMADA)
                .tiempoLimiteCheckIn(ahora.minusHours(1))
                .horaInicio(ahora.minusHours(2))
                .habitacion(h)
                .build());

        // Reserva vigente (en 1 hora)
        reservaRepository.save(Reserva.builder()
                .nombreCliente("Vigente")
                .estado(EstadoReserva.CONFIRMADA)
                .tiempoLimiteCheckIn(ahora.plusHours(1))
                .horaInicio(ahora)
                .habitacion(h)
                .build());

        List<Reserva> result = reservaRepository.findByEstadoAndTiempoLimiteCheckInBefore(EstadoReserva.CONFIRMADA, ahora);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombreCliente()).isEqualTo("Vencido");
    }
}
