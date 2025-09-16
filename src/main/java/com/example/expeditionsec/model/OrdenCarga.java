package com.example.expeditionsec.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private String destino;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estado;

    @ManyToOne
    @JoinColumn(name = "camion_id")
    private Camion camion;

    @OneToMany(mappedBy = "ordenCarga")
    private List<Palet> palets;

    public enum EstadoOrden {
        ABIERTA, EN_CARGA, CERRADA
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public List<Palet> getPalets() {
        return palets;
    }

    public void setPalets(List<Palet> palets) {
        this.palets = palets;
    }
}