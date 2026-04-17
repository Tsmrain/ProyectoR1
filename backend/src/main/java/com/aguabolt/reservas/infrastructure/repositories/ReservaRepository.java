package com.aguabolt.reservas.infrastructure.repositories;

import com.aguabolt.reservas.domain.models.Reserva;
import com.aguabolt.reservas.domain.models.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Magia de JPA para la Cola FIFO: 
    // Busca la primera reserva (First) de una habitación específica, 
    // que esté en espera, y la ordena por la fecha de creación ascendente (el más antiguo primero).
    Optional<Reserva> findFirstByHabitacionIdAndEstadoOrderByCreatedAtAsc(Long habitacionId, EstadoReserva estado);

    List<Reserva> findByEstadoAndTiempoLimiteCheckInBefore(EstadoReserva estado, LocalDateTime tiempoActual);
}
