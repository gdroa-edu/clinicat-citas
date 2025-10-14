package com.clinicat.citas.controller;

import clinicat.commons.dto.PacienteRequestDTO;
import clinicat.commons.dto.PacienteResponseDTO;
import com.clinicat.citas.service.PacientesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "API para la gestión de pacientes")
public class PacientesController {

    private static final Logger logger = LoggerFactory.getLogger(PacientesController.class);

    @Autowired
    private PacientesService pacientesService;

    @PostMapping
    @Operation(summary = "Registrar un nuevo paciente")
    @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente")
    public ResponseEntity<PacienteResponseDTO> createPaciente(@RequestBody PacienteRequestDTO pacienteDTO) {
        logger.info("Solicitud para registrar un nuevo paciente");
        return new ResponseEntity<>(pacientesService.createPaciente(pacienteDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un paciente")
    @ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente")
    public ResponseEntity<PacienteResponseDTO> updatePaciente(@PathVariable Long id, @RequestBody PacienteRequestDTO pacienteDTO) {
        logger.info("Solicitud para actualizar el paciente con ID: {}", id);
        return ResponseEntity.ok(pacientesService.updatePaciente(id, pacienteDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID")
    @ApiResponse(responseCode = "200", description = "Paciente encontrado exitosamente")
    public ResponseEntity<PacienteResponseDTO> getPacienteById(@PathVariable Long id) {
        logger.info("Solicitud para obtener el paciente con ID: {}", id);
        return ResponseEntity.ok(pacientesService.getPacienteById(id));
    }

    @GetMapping("/page/{page}")
    @Operation(summary = "Listar pacientes paginados")
    @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente")
    public ResponseEntity<Page<PacienteResponseDTO>> getAllPacientes(@PathVariable Integer page) {
        logger.info("Solicitud para obtener la página {} de pacientes", page);
        return ResponseEntity.ok(pacientesService.getAllPacientes(page));
    }

    @GetMapping("/search/{searchTerm}")
    @Operation(summary = "Buscar pacientes por nombre o usuario")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    public ResponseEntity<List<PacienteResponseDTO>> searchPacientes(@PathVariable String searchTerm) {
        logger.info("Solicitud de búsqueda de pacientes por término: {}", searchTerm);
        return ResponseEntity.ok(pacientesService.searchPacientes(searchTerm));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Realizar borrado lógico de un paciente")
    @ApiResponse(responseCode = "204", description = "Paciente eliminado exitosamente")
    public ResponseEntity<Void> deletePaciente(@PathVariable Long id) {
        logger.info("Solicitud para eliminar el paciente con ID: {}", id);
        pacientesService.deletePaciente(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(
        summary = "Obtener pacientes por ID de usuario",
        description = "Retorna todas las mascotas asociadas a un usuario específico",
        tags = { "Pacientes" }
    )
    @ApiResponse(responseCode = "200", description = "Lista de pacientes encontrada exitosamente")
    @ApiResponse(responseCode = "404", description = "No se encontraron pacientes para el usuario especificado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<PacienteResponseDTO>> getPacientesByUsuarioId(
            @Parameter(
                description = "ID del usuario propietario de las mascotas",
                required = true,
                example = "1"
            )
            @PathVariable Long usuarioId) {
        logger.info("GET /api/pacientes/usuario/{}", usuarioId);
        List<PacienteResponseDTO> pacientes = pacientesService.getPacientesByUsuarioId(usuarioId);
        return ResponseEntity.ok(pacientes);
    }
}
