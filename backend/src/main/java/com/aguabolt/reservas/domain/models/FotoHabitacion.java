package com.aguabolt.reservas.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fotos_habitacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FotoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rutaLocal;

    private boolean esPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacion_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Habitacion habitacion;
}
