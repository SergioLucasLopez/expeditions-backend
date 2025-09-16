package com.example.expeditionsec.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Palet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelo;
    private Double taraKg;
    private Double dimLargoM;
    private Double dimAnchoM;
    private String tipoProducto;

    @Enumerated(EnumType.STRING)
    private EstadoPalet estado;

    @ManyToOne
    @JoinColumn(name = "orden_id")
    private OrdenCarga ordenCarga; // relación con la orden actual

    public enum EstadoPalet {
        DISPONIBLE, ASIGNADO, EN_TRANSITO
    }

    // getters minimos que vamos a usar. Los genero porque no me funcionaba el lombok no se por que
    public Long getId() {
        return id;
    }

    public EstadoPalet getEstado() {
        return estado;
    }

    public OrdenCarga getOrdenCarga() {
        return ordenCarga;
    }

    // Setters minimos a usar

    public void setEstado(EstadoPalet estado) {
        this.estado = estado;
    }

    public void setOrdenCarga(OrdenCarga ordenCarga) {
        this.ordenCarga = ordenCarga;
    }
}