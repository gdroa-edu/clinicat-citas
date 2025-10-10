package com.clinicat.citas.controller;

import clinicat.commons.dto.CitaRequestDTO;
import clinicat.commons.dto.CitaResponseDTO;
import com.clinicat.citas.service.CitasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Citas", description = "API para la gestión de citas")
public class CitasController {

    @Autowired
    private CitasService citasService;

    @PostMapping
    @Operation(summary = "Crear una nueva cita")
    @ApiResponse(responseCode = "201", description = "Cita creada exitosamente")
    public ResponseEntity<CitaResponseDTO> createCita(@RequestBody CitaRequestDTO citaDTO) {
        return new ResponseEntity<>(citasService.createCita(citaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar una cita existente")
    @ApiResponse(responseCode = "200", description = "Cita actualizada exitosamente")
    public ResponseEntity<CitaResponseDTO> updateCita(@PathVariable Long id, @RequestBody CitaRequestDTO citaDTO) {
        return ResponseEntity.ok(citasService.updateCita(id, citaDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por ID")
    @ApiResponse(responseCode = "200", description = "Cita encontrada exitosamente")
    public ResponseEntity<CitaResponseDTO> getCitaById(@PathVariable Long id) {
        return ResponseEntity.ok(citasService.getCitaById(id));
    }

    @GetMapping("/page/{page}")
    @Operation(summary = "Listar citas paginadas")
    @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente")
    public ResponseEntity<Page<CitaResponseDTO>> getAllCitas(@PathVariable Integer page) {
        return ResponseEntity.ok(citasService.getAllCitas(page));
    }

    @GetMapping("/search/{searchTerm}")
    @Operation(summary = "Buscar citas por usuario o fecha")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    public ResponseEntity<List<CitaResponseDTO>> searchCitas(@PathVariable String searchTerm) {
        return ResponseEntity.ok(citasService.searchCitas(searchTerm));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una cita (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Cita cancelada exitosamente")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        citasService.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }
}

