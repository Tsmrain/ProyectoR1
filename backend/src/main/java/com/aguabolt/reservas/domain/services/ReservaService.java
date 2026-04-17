package com.aguabolt.reservas.domain.services;

import com.aguabolt.reservas.domain.models.EstadoHabitacion;
import com.aguabolt.reservas.domain.models.EstadoReserva;
import com.aguabolt.reservas.domain.models.Habitacion;
import com.aguabolt.reservas.domain.models.Reserva;
import com.aguabolt.reservas.domain.models.Usuario;
import com.aguabolt.reservas.infrastructure.repositories.HabitacionRepository;
import com.aguabolt.reservas.infrastructure.repositories.ReservaRepository;
import com.aguabolt.reservas.infrastructure.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(HabitacionRepository habitacionRepository, 
                          ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository) {
        this.habitacionRepository = habitacionRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Reserva crearReserva(Long habitacionId, String emailCliente, LocalDateTime horaInicio) {
        // Precondición: Existe la habitación
        Habitacion habitacion = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));

        // Precondición: Existe el usuario
        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con email: " + emailCliente));

        // Creación de la instancia
        Reserva reserva = Reserva.builder()
                .cliente(cliente)
                .horaInicio(horaInicio)
                .habitacion(habitacion)
                .build();

        // Postcondiciones basadas en el estado
        if (habitacion.getEstado() == EstadoHabitacion.DISPONIBLE) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            // Regla de negocio: 30 minutos de gracia
            reserva.setTiempoLimiteCheckIn(horaInicio.plusMinutes(30));
        } else {
            // Si está Ocupada o en Limpieza, entra a la "Cola FIFO"
            reserva.setEstado(EstadoReserva.EN_ESPERA);
            // El tiempo límite se establecerá cuando la habitación se libere y el estado pase a CONFIRMADA
        }

        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva registrarCheckIn(Long reservaId) {
        Reserva r = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        r.setEstado(EstadoReserva.OCUPADA);
        r.getHabitacion().setEstado(EstadoHabitacion.OCUPADA);
        habitacionRepository.save(r.getHabitacion());
        return reservaRepository.save(r);
    }

    @Transactional
    public Reserva registrarCheckOut(Long reservaId) {
        Reserva r = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        r.setEstado(EstadoReserva.FINALIZADA);
        r.getHabitacion().setEstado(EstadoHabitacion.LIMPIEZA);
        habitacionRepository.save(r.getHabitacion());
        return reservaRepository.save(r);
    }

    @Transactional
    public Habitacion finalizarLimpieza(Long habitacionId) {
        Habitacion h = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new IllegalArgumentException("Habitacion no encontrada"));
        
        // Logica de resolución de Cola FIFO
        reservaRepository.findFirstByHabitacionIdAndEstadoOrderByCreatedAtAsc(habitacionId, EstadoReserva.EN_ESPERA)
                .ifPresentOrElse(reservaEnEspera -> {
                    reservaEnEspera.setEstado(EstadoReserva.CONFIRMADA);
                    reservaEnEspera.setTiempoLimiteCheckIn(LocalDateTime.now().plusMinutes(30));
                    reservaRepository.save(reservaEnEspera);
                    // La habitación sigue DISPONIBLE para el siguiente, pero reservada
                    h.setEstado(EstadoHabitacion.DISPONIBLE); 
                }, () -> {
                    // Si la cola está vacía
                    h.setEstado(EstadoHabitacion.DISPONIBLE);
                });
            
        return habitacionRepository.save(h);
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void procesarNoShowsAutomatico() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Reserva> reservasVencidas = reservaRepository.findByEstadoAndTiempoLimiteCheckInBefore(EstadoReserva.CONFIRMADA, ahora);

        for (Reserva r : reservasVencidas) {
            r.setEstado(EstadoReserva.CANCELADA_POR_NO_SHOW);
            Habitacion h = r.getHabitacion();
            
            // Lógica FIFO: Si cancelamos, buscamos al siguiente en la cola
            reservaRepository.findFirstByHabitacionIdAndEstadoOrderByCreatedAtAsc(h.getId(), EstadoReserva.EN_ESPERA)
                .ifPresentOrElse(reservaEnEspera -> {
                    reservaEnEspera.setEstado(EstadoReserva.CONFIRMADA);
                    reservaEnEspera.setTiempoLimiteCheckIn(LocalDateTime.now().plusMinutes(30));
                    reservaRepository.save(reservaEnEspera);
                    h.setEstado(EstadoHabitacion.DISPONIBLE);
                }, () -> {
                    h.setEstado(EstadoHabitacion.DISPONIBLE);
                });

            habitacionRepository.save(h);
            reservaRepository.save(r);
            System.out.println("Reserva " + r.getId() + " cancelada por No-Show.");
        }
    }
}
