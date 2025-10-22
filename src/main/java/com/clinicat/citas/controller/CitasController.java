package com.clinicat.citas.controller;

import clinicat.commons.dto.CitaRequestDTO;
import clinicat.commons.dto.CitaResponseDTO;
import com.clinicat.citas.service.CitasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(CitasController.class);

    @Autowired
    private CitasService citasService;

    @PostMapping
    @Operation(summary = "Crear una nueva cita")
    @ApiResponse(responseCode = "201", description = "Cita creada exitosamente")
    public ResponseEntity<CitaResponseDTO> createCita(@RequestBody CitaRequestDTO citaDTO) {
        logger.info("Solicitud para crear una nueva cita recibida");
        return new ResponseEntity<>(citasService.createCita(citaDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar una cita existente")
    @ApiResponse(responseCode = "200", description = "Cita actualizada exitosamente")
    public ResponseEntity<CitaResponseDTO> updateCita(@PathVariable Long id, @RequestBody CitaRequestDTO citaDTO) {
        logger.info("Solicitud para modificar la cita con ID: {}", id);
        return ResponseEntity.ok(citasService.updateCita(id, citaDTO));
    }

    @PutMapping("/{id}/estado/{estadoId}")
    @Operation(summary = "Cambiar el estado de una cita")
    @ApiResponse(responseCode = "200", description = "Estado de la cita actualizado exitosamente")
    public ResponseEntity<CitaResponseDTO> changeCitaEstado(@PathVariable Long id, @PathVariable Long estadoId) {
        logger.info("Solicitud para cambiar estado de la cita ID: {} al estado ID: {}", id, estadoId);
        return ResponseEntity.ok(citasService.changeCitaEstado(id, estadoId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cita por ID")
    @ApiResponse(responseCode = "200", description = "Cita encontrada exitosamente")
    public ResponseEntity<CitaResponseDTO> getCitaById(@PathVariable Long id) {
        logger.info("Solicitud para obtener la cita con ID: {}", id);
        return ResponseEntity.ok(citasService.getCitaById(id));
    }

    @GetMapping("/page/{page}")
    @Operation(summary = "Listar citas paginadas")
    @ApiResponse(responseCode = "200", description = "Lista de citas obtenida exitosamente")
    public ResponseEntity<Page<CitaResponseDTO>> getAllCitas(@PathVariable Integer page) {
        logger.info("Solicitud para obtener la página {} de citas", page);
        return ResponseEntity.ok(citasService.getAllCitas(page));
    }

    @GetMapping("/search/fecha/{fecha}")
    @Operation(summary = "Buscar citas por fecha")
    @ApiResponse(responseCode = "200", description = "Búsqueda por fecha realizada exitosamente")
    public ResponseEntity<List<CitaResponseDTO>> searchCitasByFecha(@PathVariable String fecha) {
        logger.info("Solicitud de búsqueda de citas por fecha: {}", fecha);
        return ResponseEntity.ok(citasService.searchByFecha(fecha));
    }

    @GetMapping("/search/paciente/{nombre}")
    @Operation(summary = "Buscar citas por nombre de paciente")
    @ApiResponse(responseCode = "200", description = "Búsqueda por paciente realizada exitosamente")
    public ResponseEntity<List<CitaResponseDTO>> searchCitasByPaciente(@PathVariable String nombre) {
        logger.info("Solicitud de búsqueda de citas por nombre de paciente: {}", nombre);
        return ResponseEntity.ok(citasService.searchByPaciente(nombre));
    }

    @GetMapping("/search/propietario/{nombre}")
    @Operation(summary = "Buscar citas por nombre de propietario")
    @ApiResponse(responseCode = "200", description = "Búsqueda por propietario realizada exitosamente")
    public ResponseEntity<List<CitaResponseDTO>> searchCitasByPropietario(@PathVariable String nombre) {
        logger.info("Solicitud de búsqueda de citas por nombre de propietario: {}", nombre);
        return ResponseEntity.ok(citasService.searchByPropietario(nombre));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una cita (borrado lógico)")
    @ApiResponse(responseCode = "204", description = "Cita cancelada exitosamente")
    public ResponseEntity<Void> cancelarCita(@PathVariable Long id) {
        logger.info("Solicitud para cancelar la cita con ID: {}", id);
        citasService.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }

    }

    @GetMapping("/estado/{estadoId}")
    @Operation(summary = "Obtener citas por ID de estado")
    @ApiResponse(responseCode = "200", description = "Citas encontradas exitosamente")
    @ApiResponse(responseCode = "404", description = "No se encontraron citas para el estado especificado")
    public ResponseEntity<List<CitaResponseDTO>> getCitasByEstadoId(@PathVariable Long estadoId) {
        logger.info("Solicitud para obtener citas con estado ID: {}", estadoId);
        List<CitaResponseDTO> citas = citasService.getCitasByEstadoId(estadoId);
        return ResponseEntity.ok(citas);
    }
}
