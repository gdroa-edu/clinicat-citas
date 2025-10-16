package com.clinicat.citas.service;

import clinicat.commons.dto.CitaDetalleResponseDTO;
import clinicat.commons.dto.CitaRequestDTO;
import clinicat.commons.dto.CitaResponseDTO;
import clinicat.commons.dto.CitaDetalleRequestDTO;
import clinicat.commons.entity.CitaEntity;
import clinicat.commons.entity.CitaDetalleEntity;
import clinicat.commons.entity.EstadoCitaEntity;
import clinicat.commons.entity.UsuarioEntity;
import clinicat.commons.entity.HorarioEntity;
import clinicat.commons.entity.ProductoServicioEntity;
import com.clinicat.citas.repository.ICitasRepository;
import com.clinicat.citas.repository.ICitaDetallesRepository;
import com.clinicat.citas.repository.IEstadosCitasRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class CitasService {
    private static final Logger log = LoggerFactory.getLogger(CitasService.class);

    @Value("${app.pagination.page-size}")
    private Integer pageSize;

    @Autowired
    private ICitasRepository citasRepository;

    @Autowired
    private ICitaDetallesRepository citaDetallesRepository;

    @Autowired
    private IEstadosCitasRepository estadosCitasRepository;

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

        // Actualizar campos principales de la cita
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
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Estado no encontrado con ID: " + citaDTO.getEstadoId()));
            existingCita.setEstado(estado);
        }
        if (citaDTO.getVeterinarioId() != null) {
            UsuarioEntity veterinario = entityManager.find(UsuarioEntity.class, citaDTO.getVeterinarioId());
            if (veterinario != null) {
                existingCita.setVeterinario(veterinario);
            }
        }

        // Eliminar detalles existentes
        citaDetallesRepository.deleteAllByCitaId(id);
        entityManager.flush();

        // Crear y guardar nuevos detalles si existen
        if (citaDTO.getDetalles() != null && !citaDTO.getDetalles().isEmpty()) {
            List<CitaDetalleEntity> detalles = new ArrayList<>();
            for (CitaDetalleRequestDTO detalleDTO : citaDTO.getDetalles()) {
                CitaDetalleEntity detalle = new CitaDetalleEntity();
                detalle.setCita(existingCita);

                ProductoServicioEntity productoServicio = entityManager.find(ProductoServicioEntity.class, detalleDTO.getServicioId());
                if (productoServicio == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Servicio no encontrado con ID: " + detalleDTO.getServicioId());
                }
                detalle.setServicio(productoServicio);
                detalle.setDescripcion(detalleDTO.getDescripcion());

                detalles.add(detalle);
            }
            citaDetallesRepository.saveAll(detalles);
        }

        // Guardar la cita actualizada
        CitaEntity updatedCita = citasRepository.save(existingCita);
        entityManager.flush();

        return getCitaById(updatedCita.getId());
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

    // Nuevo método: cambiar el estado de una cita
    public CitaResponseDTO changeCitaEstado(Long citaId, Long estadoId) {
        log.info("Cambiando estado de la cita ID: {} al estado ID: {}", citaId, estadoId);

        CitaEntity cita = citasRepository.findById(citaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la cita con id: " + citaId));

        EstadoCitaEntity estado = estadosCitasRepository.findById(estadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el estado con id: " + estadoId));

        cita.setEstado(estado);
        CitaEntity updated = citasRepository.save(cita);
        entityManager.flush();

        return modelMapper.map(updated, CitaResponseDTO.class);
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

    public List<CitaResponseDTO> searchByFecha(String fechaStr) {
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaStr);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de fecha inválido. Use yyyy-MM-dd.");
        }

        List<CitaEntity> citas = citasRepository.findByFecha(fecha);
        if (citas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron citas para la fecha: " + fechaStr);
        }
        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<CitaResponseDTO> searchByPaciente(String nombre) {
        List<CitaEntity> citas = citasRepository.findByPacienteNombre(nombre);
        if (citas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron citas para el paciente: " + nombre);
        }
        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<CitaResponseDTO> searchByPropietario(String nombre) {
        List<CitaEntity> citas = citasRepository.findByPropietarioNombre(nombre);
        if (citas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron citas para el propietario: " + nombre);
        }
        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<CitaDetalleResponseDTO> getDetallesByCitaId(Long citaId) {
        log.info("Buscando detalles para la cita ID: {}", citaId);

        // Primero verificamos que la cita exista
        citasRepository.findById(citaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró la cita con id: " + citaId));

        List<CitaDetalleEntity> detalles = citaDetallesRepository.findByCitaId(citaId);

        if (detalles.isEmpty()) {
            log.warn("No se encontraron detalles para la cita ID: {}", citaId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron detalles para la cita especificada");
        }

        return detalles.stream()
                .map(detalle -> modelMapper.map(detalle, CitaDetalleResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<CitaResponseDTO> getCitasByVeterinarioId(Long veterinarioId) {
        log.info("Buscando citas para el veterinario con ID: {}", veterinarioId);
        List<CitaEntity> citas = citasRepository.findByVeterinarioId(veterinarioId);
        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<CitaResponseDTO> getCitasByEstadoId(Long estadoId) {
        log.info("Buscando citas para el estado con ID: {}", estadoId);

        // Verificar que el estado existe
        estadosCitasRepository.findById(estadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No se encontró el estado con id: " + estadoId));

        List<CitaEntity> citas = citasRepository.findByEstadoId(estadoId);

        if (citas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No se encontraron citas con el estado especificado");
        }

        return citas.stream()
                .map(cita -> modelMapper.map(cita, CitaResponseDTO.class))
                .collect(Collectors.toList());
    }
}
