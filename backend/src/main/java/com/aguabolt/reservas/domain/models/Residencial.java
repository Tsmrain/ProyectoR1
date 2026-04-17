package com.aguabolt.reservas.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "residenciales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Residencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    // Campos para Integración con Google Maps
    private Double latitud;
    private Double longitud;

    @Column(nullable = false)
    private Integer tiempoXPredeterminado;

    @OneToMany(mappedBy = "residencial", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Habitacion> habitaciones = new ArrayList<>();
}
