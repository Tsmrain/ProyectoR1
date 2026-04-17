package com.aguabolt.reservas.controllers;

import com.aguabolt.reservas.domain.models.Reserva;
import com.aguabolt.reservas.domain.services.ReservaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<Reserva> crearReserva(
            @RequestParam Long habitacionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime horaInicio,
            Principal principal) {
        
        String emailCliente = principal.getName();
        Reserva nuevaReserva = reservaService.crearReserva(habitacionId, emailCliente, horaInicio);
        return ResponseEntity.ok(nuevaReserva);
    }

    @PutMapping("/{id}/checkin")
    public ResponseEntity<Reserva> registrarCheckIn(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.registrarCheckIn(id));
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<Reserva> registrarCheckOut(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.registrarCheckOut(id));
    }

    @PostMapping("/procesar-no-shows")
    public ResponseEntity<String> forzarProcesamientoNoShows() {
        reservaService.procesarNoShowsAutomatico();
        return ResponseEntity.ok("Procesamiento de No-Shows ejecutado con éxito.");
    }
}
