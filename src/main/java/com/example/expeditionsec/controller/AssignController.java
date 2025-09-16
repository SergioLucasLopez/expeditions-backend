package com.example.expeditionsec.controller;

import com.example.expeditionsec.dto.AssignPalletRequest;
import com.example.expeditionsec.dto.AssignPalletResponse;
import com.example.expeditionsec.service.AssignService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class AssignController {

    // Pongo el constructor de forma manual porque por algún casual el lombook no me funcionada y quite la etiqueta RequiredArgsConstructor
    public AssignController(AssignService service) {
        this.service = service;
    }

    private final AssignService service;

    @PostMapping("/pallet-to-order")
    public ResponseEntity<AssignPalletResponse> palletToOrder(@RequestBody AssignPalletRequest req) {
        return ResponseEntity.ok(service.assignPalletToOrder(req));
    }
}