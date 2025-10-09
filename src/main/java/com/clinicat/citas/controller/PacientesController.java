package com.clinicat.citas.controller;

import clinicat.commons.dto.PacienteRequestDTO;
import clinicat.commons.dto.PacienteResponseDTO;
import com.clinicat.citas.service.PacientesService;
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
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "API para la gestión de pacientes")
public class PacientesController {

    @Autowired
    private PacientesService pacientesService;

    @PostMapping
    @Operation(summary = "Registrar un nuevo paciente")
    @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente")
    public ResponseEntity<PacienteResponseDTO> createPaciente(@RequestBody PacienteRequestDTO pacienteDTO) {
        return new ResponseEntity<>(pacientesService.createPaciente(pacienteDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un paciente")
    @ApiResponse(responseCode = "200", description = "Paciente actualizado exitosamente")
    public ResponseEntity<PacienteResponseDTO> updatePaciente(@PathVariable Long id, @RequestBody PacienteRequestDTO pacienteDTO) {
        return ResponseEntity.ok(pacientesService.updatePaciente(id, pacienteDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener paciente por ID")
    @ApiResponse(responseCode = "200", description = "Paciente encontrado exitosamente")
    public ResponseEntity<PacienteResponseDTO> getPacienteById(@PathVariable Long id) {
        return ResponseEntity.ok(pacientesService.getPacienteById(id));
    }

    @GetMapping("/page/{page}")
    @Operation(summary = "Listar pacientes paginados")
    @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente")
    public ResponseEntity<Page<PacienteResponseDTO>> getAllPacientes(@PathVariable Integer page) {
        return ResponseEntity.ok(pacientesService.getAllPacientes(page));
    }

    @GetMapping("/search/{searchTerm}")
    @Operation(summary = "Buscar pacientes por nombre o usuario")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    public ResponseEntity<List<PacienteResponseDTO>> searchPacientes(@PathVariable String searchTerm) {
        return ResponseEntity.ok(pacientesService.searchPacientes(searchTerm));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Realizar borrado lógico de un paciente")
    @ApiResponse(responseCode = "204", description = "Paciente eliminado exitosamente")
    public ResponseEntity<Void> deletePaciente(@PathVariable Long id) {
        pacientesService.deletePaciente(id);
        return ResponseEntity.noContent().build();
    }
}