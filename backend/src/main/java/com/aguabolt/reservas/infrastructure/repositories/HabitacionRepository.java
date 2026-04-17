package com.aguabolt.reservas.infrastructure.repositories;

import com.aguabolt.reservas.domain.models.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
}
