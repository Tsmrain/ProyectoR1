package com.aguabolt.reservas.controllers;

import com.aguabolt.reservas.domain.models.Habitacion;
import com.aguabolt.reservas.domain.services.ReservaService;
import com.aguabolt.reservas.infrastructure.repositories.HabitacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/habitaciones")
@CrossOrigin(origins = "*")
public class HabitacionController {

    private final HabitacionRepository habitacionRepository;
    private final ReservaService reservaService;

    public HabitacionController(HabitacionRepository habitacionRepository, ReservaService reservaService) {
        this.habitacionRepository = habitacionRepository;
        this.reservaService = reservaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> getHabitacion(@PathVariable Long id) {
        return habitacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/finalizar-limpieza")
    public ResponseEntity<Habitacion> finalizarLimpieza(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.finalizarLimpieza(id));
    }
}
