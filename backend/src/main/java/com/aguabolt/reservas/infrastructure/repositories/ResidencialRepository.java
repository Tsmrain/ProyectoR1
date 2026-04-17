package com.aguabolt.reservas.infrastructure.repositories;

import com.aguabolt.reservas.domain.models.Residencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidencialRepository extends JpaRepository<Residencial, Long> {
}
