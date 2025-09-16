package com.example.expeditionsec.controller;

import com.example.expeditionsec.dto.OrderOption;
import com.example.expeditionsec.model.OrdenCarga;
import com.example.expeditionsec.repository.OrdenCargaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrdenCargaRepository repo;

    public OrderController(OrdenCargaRepository repo) {
        this.repo = repo;
    }

    // Devuelve órdenes ABIERTA o EN_CARGA
    @GetMapping("/open")
    public List<OrderOption> openOrders() {
        return repo.findAll().stream()
                .filter(o -> o.getEstado() == OrdenCarga.EstadoOrden.ABIERTA
                        || o.getEstado() == OrdenCarga.EstadoOrden.EN_CARGA)
                .map(o -> new OrderOption(
                        o.getId(),
                        o.getCodigo(),
                        o.getDestino(),
                        o.getCamion() != null ? o.getCamion().getId() : null,
                        o.getCamion() != null ? o.getCamion().getMatricula() : null
                ))
                .toList();
    }
}