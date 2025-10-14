package com.clinicat.citas.controller;

import clinicat.commons.dto.CitaDetalleResponseDTO;
import com.clinicat.citas.service.CitasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Detalles de Citas", description = "API para gestionar los detalles de las citas")
public class CitasDetallesController {
    private static final Logger log = LoggerFactory.getLogger(CitasDetallesController.class);

    @Autowired
    private CitasService citasService;

    @GetMapping("/citas/{citaId}/detalles")
    @Operation(
        summary = "Obtener detalles por ID de cita",
        description = "Retorna todos los detalles asociados a una cita específica"
    )
    @ApiResponse(responseCode = "200", description = "Detalles encontrados exitosamente")
    @ApiResponse(responseCode = "404", description = "No se encontraron detalles para la cita especificada")
    public ResponseEntity<List<CitaDetalleResponseDTO>> getDetallesByCitaId(
            @Parameter(description = "ID de la cita", required = true)
            @PathVariable Long citaId) {
        log.info("GET /api/citas/{}/detalles", citaId);
        return ResponseEntity.ok(citasService.getDetallesByCitaId(citaId));
    }
}
