package com.clinicat.citas.service;

import clinicat.commons.dto.PacienteRequestDTO;
import clinicat.commons.dto.PacienteResponseDTO;
import clinicat.commons.entity.PacienteEntity;
import clinicat.commons.entity.RazaEntity;
import clinicat.commons.entity.UsuarioEntity;
import com.clinicat.citas.repository.IPacientesRepository;
import com.clinicat.citas.repository.IRazasRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PacientesService {

    @Value("${app.pagination.page-size}")
    private Integer pageSize;

    @Autowired
    private IPacientesRepository pacientesRepository;

    @Autowired
    private IRazasRepository razasRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ModelMapper modelMapper;

    public PacienteResponseDTO createPaciente(PacienteRequestDTO pacienteDTO) {
        PacienteEntity entity = modelMapper.map(pacienteDTO, PacienteEntity.class);
        entity.setEliminado(false);
        PacienteEntity savedEntity = pacientesRepository.save(entity);
        return getPacienteById(savedEntity.getId());
    }

    public PacienteResponseDTO updatePaciente(Long id, PacienteRequestDTO pacienteDTO) {
        PacienteEntity existingPaciente = pacientesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el paciente con id: " + id));

        // Actualizar campos básicos manualmente (sin ModelMapper para evitar conflictos)
        existingPaciente.setNombre(pacienteDTO.getNombre());
        existingPaciente.setSexo(pacienteDTO.getSexo());
        existingPaciente.setDescripcion(pacienteDTO.getDescripcion());

        // Actualizar la raza si se proporciona un razaId diferente
        if (pacienteDTO.getRazaId() != null &&
            (existingPaciente.getRaza() == null || !existingPaciente.getRaza().getId().equals(pacienteDTO.getRazaId()))) {
            RazaEntity raza = razasRepository.findById(pacienteDTO.getRazaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "No se encontró la raza con id: " + pacienteDTO.getRazaId()));
            existingPaciente.setRaza(raza);
        }

        // Actualizar el usuario si se proporciona un usuarioId diferente
        if (pacienteDTO.getUsuarioId() != null &&
            (existingPaciente.getUsuario() == null || !existingPaciente.getUsuario().getId().equals(pacienteDTO.getUsuarioId()))) {
            UsuarioEntity usuario = entityManager.find(UsuarioEntity.class, pacienteDTO.getUsuarioId());
            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el usuario con id: " + pacienteDTO.getUsuarioId());
            }
            existingPaciente.setUsuario(usuario);
        }

        PacienteEntity savedEntity = pacientesRepository.save(existingPaciente);
        return getPacienteById(savedEntity.getId());
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
        entityManager.flush();
    }
}
