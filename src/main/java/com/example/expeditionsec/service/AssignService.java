package com.example.expeditionsec.service;

import com.example.expeditionsec.dto.AssignPalletRequest;
import com.example.expeditionsec.dto.AssignPalletResponse;
import com.example.expeditionsec.exception.BusinessException;
import com.example.expeditionsec.exception.NotFoundException;
import com.example.expeditionsec.model.Camion;
import com.example.expeditionsec.model.OrdenCarga;
import com.example.expeditionsec.model.Palet;
import com.example.expeditionsec.repository.OrdenCargaRepository;
import com.example.expeditionsec.repository.PaletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignService {


    private final PaletRepository paletRepo;
    private final OrdenCargaRepository ordenRepo;

    // Vuelvo a ponerlo manualmente porque me fallaba el loombok
    public AssignService(PaletRepository paletRepo, OrdenCargaRepository ordenRepo) {
        this.paletRepo = paletRepo;
        this.ordenRepo = ordenRepo;
    }

    /**
     * Asignamos un palet a una orden de carga (la orden debe tener camión).
     * Reglas:
     * - Palet existe y está DISPONIBLE.
     * - Orden existe, está ABIERTA o EN_CARGA.
     * - La orden debe tener camión asociado.
     * Efectos:
     * - palet.estado = ASIGNADO
     * - palet.ordenCarga = orden
     */
    @Transactional
    public AssignPalletResponse assignPalletToOrder(AssignPalletRequest req) {

        // Comprobamos que se han pasado los 2 parámetros que se requiere. A modo de programación defensiva
        if (req.palletId() == null || req.orderId() == null) {
            throw new BusinessException("palletId y orderId son obligatorios");
        }

        /*Puesto que debemos pasar un id de camion y un id de palet para que en la ordenn quede el palet asignado
        * comenzamos con identificar primero si en el repositorio, se encuentra el palet*/
        Palet palet = paletRepo.findById(req.palletId())
                .orElseThrow(() -> new NotFoundException("No existe palet id=" + req.palletId()));

        // Para evitar una doble asignación (porque el palet ya está asignado a esa orden, hacemos lo siguiente)
        if (palet.getOrdenCarga() != null) {
            Long ordenActualId = palet.getOrdenCarga().getId();
            if (ordenActualId.equals(req.orderId())) {
                throw new BusinessException("El palet " + palet.getId() + " ya está asignado a la orden " + ordenActualId);
            } else {
                throw new BusinessException("El palet " + palet.getId() + " ya está asignado a otra orden (" + ordenActualId + ")");
            }
        }

        // Si no está disponible el palet, lo comunicamos
        if (palet.getEstado() != Palet.EstadoPalet.DISPONIBLE) {
            throw new BusinessException("El palet " + palet.getId() + " no está DISPONIBLE (estado actual: " + palet.getEstado() + ")");
        }

        // Luego vamos a ver si existe la orden (que es como buscar un camión, puesto que una orden tiene un camión asignado)
        OrdenCarga orden = ordenRepo.findById(req.orderId())
                .orElseThrow(() -> new NotFoundException("No existe orden id=" + req.orderId()));

        // Si no hay orden, se informa de ello
        if (orden.getEstado() == OrdenCarga.EstadoOrden.CERRADA) {
            throw new BusinessException("La orden " + orden.getId() + " está CERRADA");
        }

        // En caso de haber orden, pues cogemos el camión asignado a la orden. No obstante, comprobamos si tiene camión asignado la orden
        Camion camion = orden.getCamion();
        if (camion == null) {
            throw new BusinessException("La orden " + orden.getId() + " no tiene camión asociado");
        }

        // Asignamos al palet que hemos pasado dicha orden, lo ponemos con el estado asignado y guardamos en el repo.
        palet.setOrdenCarga(orden);
        palet.setEstado(Palet.EstadoPalet.ASIGNADO);
        paletRepo.save(palet);

        return new AssignPalletResponse(palet.getId(), orden.getId(), camion.getId(),
                "Palet " + palet.getId() + " asignado a la orden " + orden.getId() + " (camión " + camion.getId() + ")");
    }
}