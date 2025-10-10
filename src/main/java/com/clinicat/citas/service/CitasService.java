package com.clinicat.citas.service;

import clinicat.commons.dto.CitaRequestDTO;
import clinicat.commons.dto.CitaResponseDTO;
import clinicat.commons.dto.CitaDetalleRequestDTO;
import clinicat.commons.entity.CitaEntity;
import clinicat.commons.entity.CitaDetalleEntity;
import clinicat.commons.entity.EstadoCitaEntity;
import clinicat.commons.entity.PacienteEntity;
import clinicat.commons.entity.UsuarioEntity;
import clinicat.commons.entity.HorarioEntity;
import clinicat.commons.entity.ProductoServicioEntity;
import com.clinicat.citas.repository.ICitasRepository;
import com.clinicat.citas.repository.ICitaDetallesRepository;
import com.clinicat.citas.repository.IEstadosCitasRepository;
import com.clinicat.citas.repository.IPacientesRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CitasService {

    @Value("${app.pagination.page-size}")
    private Integer pageSize;

    @Autowired
    private ICitasRepository citasRepository;

    @Autowired
    private ICitaDetallesRepository citaDetallesRepository;

    @Autowired
    private IEstadosCitasRepository estadosCitasRepository;

    @Autowired
    private IPacientesRepository pacientesRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public CitaResponseDTO createCita(CitaRequestDTO citaDTO) {
        CitaEntity cita = modelMapper.map(citaDTO, CitaEntity.class);

        // Guardar la cita para obtener un ID
        CitaEntity savedCita = citasRepository.save(cita);
        entityManager.flush(); // Asegura que el ID de la cita esté disponible

        if (citaDTO.getDetalles() != null && !citaDTO.getDetalles().isEmpty()) {
            List<CitaDetalleEntity> detalles = new ArrayList<>();
            for (CitaDetalleRequestDTO detalleDTO : citaDTO.getDetalles()) {
                CitaDetalleEntity detalle = new CitaDetalleEntity();

                // Asignar la cita guardada al detalle
                detalle.setCita(savedCita);

                // Buscar y asignar el producto/servicio
                ProductoServicioEntity productoServicio = entityManager.find(ProductoServicioEntity.class, detalleDTO.getServicioId());
                if (productoServicio == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio con ID " + detalleDTO.getServicioId() + " no encontrado.");
                }
                detalle.setServicio(productoServicio);
                detalle.setDescripcion(detalleDTO.getDescripcion());

                detalles.add(detalle);
            }
            // Guardar todos los detalles
            citaDetallesRepository.saveAll(detalles);
        }

        return getCitaById(savedCita.getId());
    }

    public CitaResponseDTO updateCita(Long id, CitaRequestDTO citaDTO) {
        CitaEntity existingCita = citasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la cita con id: " + id));

        if (citaDTO.getFechaHora() != null) {
            existingCita.setFechaHora(citaDTO.getFechaHora());
        }

        if (citaDTO.getHorarioId() != null) {
            HorarioEntity horario = entityManager.find(HorarioEntity.class, citaDTO.getHorarioId());
            if (horario != null) {
                existingCita.setHorario(horario);
            }
        }

        if (citaDTO.getEstadoId() != null) {
            EstadoCitaEntity estado = estadosCitasRepository.findById(citaDTO.getEstadoId())
                    .orElse(null);
            if (estado != null) {
                existingCita.setEstado(estado);
            }
        }

        citasRepository.save(existingCita);
        return getCitaById(id);
    }

    public CitaResponseDTO getCitaById(Long id) {
        CitaEntity cita = citasRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la cita con id: " + id));

        return modelMapper.map(cita, CitaResponseDTO.class);
    }

    public Page<CitaResponseDTO> getAllCitas(Integer page) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return citasRepository.findAll(pageRequest)
                .map(cita -> modelMapper.map(cita, CitaResponseDTO.class));
    }

    public List<CitaResponseDTO> searchCitas(String searchTerm) {
        List<CitaEntity> citas = citasRepository.findByUsuarioOrFecha(searchTerm);

        if (citas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }

        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void cancelarCita(Long id) {
        CitaEntity cita = citasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la cita con id: " + id));

        EstadoCitaEntity estadoCancelada = estadosCitasRepository.findAll().stream()
                .filter(estado -> {
                    try {
                        String nombreEstado = (String) estado.getClass().getMethod("getNombre").invoke(estado);
                        return nombreEstado != null && nombreEstado.equalsIgnoreCase("cancelada");
                    } catch (Exception e) {
                        return false;
                    }
                })
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el estado 'cancelada'"));

        cita.setEstado(estadoCancelada);
        citasRepository.save(cita);
        entityManager.flush();
    }
}
