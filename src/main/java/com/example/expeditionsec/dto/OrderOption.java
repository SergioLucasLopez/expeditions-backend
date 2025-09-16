package com.example.expeditionsec.dto;

public record OrderOption(
        Long id,
        String codigo,
        String destino,
        Long truckId,
        String truckPlate
) {}