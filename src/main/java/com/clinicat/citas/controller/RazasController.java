package com.clinicat.citas.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.clinicat.citas.service.RazasService;
import clinicat.commons.dto.RazaDTO;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Razas", description = "API para la gestión de razas")
public class RazasController {

    @Autowired
    private RazasService razasService;

    @GetMapping("/api/razas/page/{page}")
    @Operation(summary = "Obtener razas paginadas")
    @ApiResponse(responseCode = "200", description = "Lista de razas obtenida exitosamente")
    public ResponseEntity<Page<RazaDTO>> getAllRazas(@PathVariable Integer page) {
        return ResponseEntity.ok(razasService.getAllRazas(page));
    }

    @GetMapping("api/razas/{id}")
    @Operation(summary = "Obtener raza por id")
    @ApiResponse(responseCode = "200", description = "Raza obtenida exitosamente")
    public ResponseEntity<RazaDTO> getRazaById(@PathVariable Long id) {
        return ResponseEntity.ok(razasService.getRazaById(id));
    }
}
