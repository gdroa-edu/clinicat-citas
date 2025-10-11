package com.clinicat.citas.controller;

import clinicat.commons.dto.EstadoCitaDTO;
import clinicat.commons.dto.RazaDTO;
import com.clinicat.citas.service.EstadosCitaService;
import com.clinicat.citas.service.RazasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "EstadosCita", description = "API para la gestión de estads de citas")
public class EstadosCitaController {

    private static final Logger logger = LoggerFactory.getLogger(EstadosCitaController.class);

    @Autowired
    private EstadosCitaService ecitasService;

    @GetMapping("/api/estados/page/{page}")
    @Operation(summary = "Obtener estados paginados")
    @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente")
    public ResponseEntity<Page<EstadoCitaDTO>> getAllRazas(@PathVariable Integer page) {
        logger.info("Solicitud para obtener la página {} de estados de cita", page);
        return ResponseEntity.ok(ecitasService.getAllEstados(page));
    }

    @GetMapping("api/estados/{id}")
    @Operation(summary = "Obtener estado por id")
    @ApiResponse(responseCode = "200", description = "Estado obtenido exitosamente")
    public ResponseEntity<EstadoCitaDTO> getRazaById(@PathVariable Long id) {
        logger.info("Solicitud para obtener el estado de cita con ID: {}", id);
        return ResponseEntity.ok(ecitasService.getEstadoCitaById(id));
    }
}
