package com.aguabolt.reservas.controllers;

import com.aguabolt.reservas.domain.models.FotoHabitacion;
import com.aguabolt.reservas.domain.models.Habitacion;
import com.aguabolt.reservas.domain.services.FileStorageService;
import com.aguabolt.reservas.domain.services.ReservaService;
import com.aguabolt.reservas.infrastructure.repositories.FotoHabitacionRepository;
import com.aguabolt.reservas.infrastructure.repositories.HabitacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionRepository habitacionRepository;
    private final ReservaService reservaService;
    private final FileStorageService fileStorageService;
    private final FotoHabitacionRepository fotoHabitacionRepository;

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

    @PostMapping("/{id}/fotos")
    public ResponseEntity<FotoHabitacion> subirFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "esPrincipal", defaultValue = "false") boolean esPrincipal) {
        
        Habitacion h = habitacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));
        
        String url = fileStorageService.guardarArchivo(file);
        
        FotoHabitacion foto = FotoHabitacion.builder()
                .rutaLocal(url)
                .esPrincipal(esPrincipal)
                .habitacion(h)
                .build();
        
        return ResponseEntity.status(201).body(fotoHabitacionRepository.save(foto));
    }
}
