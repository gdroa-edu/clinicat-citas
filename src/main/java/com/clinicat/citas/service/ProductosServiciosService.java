package com.clinicat.citas.service;

import clinicat.commons.dto.ProductoServicioDTO;
import clinicat.commons.dto.RazaDTO;
import com.clinicat.citas.repository.IProductosServiciosRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductosServiciosService {

    @Value("${app.pagination.page-size}")
    private Integer pageSize;

    @Autowired
    private IProductosServiciosRepository psrepository;

    @Autowired
    private ModelMapper modelMapper;

    public Page<ProductoServicioDTO> getAllProductosServicios(Integer page) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<ProductoServicioDTO> pservicios = psrepository.findAll(pageRequest)
                .map(entity -> modelMapper.map(entity, ProductoServicioDTO.class));
        if (pservicios.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }
        return pservicios;
    }

    public ProductoServicioDTO getProductoServicioById(Long id) {
        return psrepository.findById(id)
                .map(entity -> modelMapper.map(entity, ProductoServicioDTO.class))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "No se encontro item con id: " + id));
    }

    public List<ProductoServicioDTO> searchProductosServicios(String nombre_producto) {
        List<ProductoServicioDTO> pservicios = psrepository.findByNombreProductoContainingIgnoreCase(nombre_producto)
                .stream()
                .map(entity -> modelMapper.map(entity, ProductoServicioDTO.class))
                .collect(Collectors.toList());

        if (pservicios.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }
        return pservicios;
    }
}
