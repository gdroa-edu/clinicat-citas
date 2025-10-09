package com.clinicat.citas.service;

import clinicat.commons.dto.PacienteRequestDTO;
import clinicat.commons.dto.PacienteResponseDTO;
import clinicat.commons.entity.PacienteEntity;
import com.clinicat.citas.repository.IPacientesRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PacientesService {

    @Value("${app.pagination.page-size}")
    private Integer pageSize;

    @Autowired
    private IPacientesRepository pacientesRepository;

    @Autowired
    private ModelMapper modelMapper;

    public PacienteResponseDTO createPaciente(PacienteRequestDTO pacienteDTO) {
        PacienteEntity entity = modelMapper.map(pacienteDTO, PacienteEntity.class);
        entity.setEliminado(false);
        PacienteEntity savedEntity = pacientesRepository.save(entity);
        return getPacienteById(savedEntity.getId()); // Usar el método que carga las relaciones
    }

    public PacienteResponseDTO updatePaciente(Long id, PacienteRequestDTO pacienteDTO) {
        PacienteEntity existingPaciente = pacientesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el paciente con id: " + id));

        // Mantener el estado de eliminado original
        boolean eliminadoOriginal = existingPaciente.getEliminado();

        modelMapper.map(pacienteDTO, existingPaciente);
        existingPaciente.setId(id);
        existingPaciente.setEliminado(eliminadoOriginal);

        return modelMapper.map(pacientesRepository.save(existingPaciente), PacienteResponseDTO.class);
    }

    public PacienteResponseDTO getPacienteById(Long id) {
        return pacientesRepository.findByIdWithRelations(id)
                .map(entity -> modelMapper.map(entity, PacienteResponseDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el paciente con id: " + id));
    }

    public Page<PacienteResponseDTO> getAllPacientes(Integer page) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<PacienteResponseDTO> pacientes = pacientesRepository.findAll(pageRequest)
                .map(entity -> modelMapper.map(entity, PacienteResponseDTO.class));

        if (pacientes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }
        return pacientes;
    }

    public List<PacienteResponseDTO> searchPacientes(String searchTerm) {
        List<PacienteEntity> pacientes = pacientesRepository
                .findByNombreOrUsuarioNombreContaining(searchTerm);

        if (pacientes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }

        return pacientes.stream()
                .map(entity -> modelMapper.map(entity, PacienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void deletePaciente(Long id) {
        PacienteEntity paciente = pacientesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el paciente con id: " + id));

        paciente.setEliminado(true);
        pacientesRepository.save(paciente);
    }
}