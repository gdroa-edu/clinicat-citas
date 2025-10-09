package com.clinicat.citas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import clinicat.commons.dto.RazaDTO;
import com.clinicat.citas.repository.IRazasRepository;
import org.modelmapper.ModelMapper;

@Service
public class RazasService {

    @Value("${app.pagination.page-size}")
    private Integer pageSize;

    @Autowired
    private IRazasRepository IRazasRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<RazaDTO> getAllRazas(Integer page) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<RazaDTO> razas = IRazasRepository.findAll(pageRequest)
                .map(entity -> modelMapper.map(entity, RazaDTO.class));

        if (razas.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }
        return razas;
    }

    public RazaDTO getRazaById(Long id) {
        return IRazasRepository.findById(id)
                .map(entity -> modelMapper.map(entity, RazaDTO.class))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "No se encontro la raza con id: " + id));
    }
}
