package com.clinicat.citas.service;

import clinicat.commons.dto.EstadoCitaDTO;
import clinicat.commons.dto.RazaDTO;
import com.clinicat.citas.repository.IEstadosCitasRepository;
import com.clinicat.citas.repository.IRazasRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EstadosCitaService {

    @Value("${app.pagination.page-size}")
    private Integer pageSize;

    @Autowired
    private IEstadosCitasRepository IEstadosCitasRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<EstadoCitaDTO> getAllEstados(Integer page) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<EstadoCitaDTO> razas = IEstadosCitasRepository.findAll(pageRequest)
                .map(entity -> modelMapper.map(entity, EstadoCitaDTO.class));

        if (razas.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }
        return razas;
    }

    public EstadoCitaDTO getEstadoCitaById(Long id) {
        return IEstadosCitasRepository.findById(id)
                .map(entity -> modelMapper.map(entity, EstadoCitaDTO.class))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "No se encontro el estado con id: " + id));
    }


}
