package com.aguabolt.reservas;

import com.aguabolt.reservas.domain.models.EstadoHabitacion;
import com.aguabolt.reservas.domain.models.Habitacion;
import com.aguabolt.reservas.infrastructure.repositories.HabitacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HabitacionRepository habitacionRepository;

    public DataInitializer(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (habitacionRepository.count() == 0) {
            Habitacion h1 = Habitacion.builder()
                    .numero("101")
                    .estado(EstadoHabitacion.DISPONIBLE)
                    .tarifa(new BigDecimal("150.00"))
                    .tiempoXLimpieza(30)
                    .build();
            habitacionRepository.save(h1);
            System.out.println("Base de datos inicializada con la habitacion 101.");
        }
    }
}
