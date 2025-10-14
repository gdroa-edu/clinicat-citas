package com.clinicat.citas.service;

import clinicat.commons.dto.ProductoServicioDTO;
import clinicat.commons.entity.ProductoServicioEntity;
import com.clinicat.citas.repository.IProductosServiciosRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ProductosServiciosService {
    private static final Logger log = LoggerFactory.getLogger(ProductosServiciosService.class);

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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }
        return pservicios;
    }

    public ProductoServicioDTO getProductoServicioById(Long id) {
        return psrepository.findById(id)
                .map(entity -> modelMapper.map(entity, ProductoServicioDTO.class))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se encontro item con id: " + id));
    }

    public List<ProductoServicioDTO> searchProductosServicios(String nombreProducto) {
        List<ProductoServicioDTO> pservicios = psrepository.findByNombreProductoContainingIgnoreCase(nombreProducto)
                .stream()
                .map(entity -> modelMapper.map(entity, ProductoServicioDTO.class))
                .collect(Collectors.toList());

        if (pservicios.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron resultados");
        }
        return pservicios;
    }

    public List<ProductoServicioDTO> getByTipo(String tipo) {
        log.info("Obteniendo {} de la base de datos", tipo);
        List<ProductoServicioEntity> items = psrepository.findByTipoIgnoreCase(tipo);

        if (items.isEmpty()) {
            log.warn("No se encontraron {} en la base de datos", tipo);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("No se encontraron %s", tipo));
        }

        return items.stream()
                .map(entity -> modelMapper.map(entity, ProductoServicioDTO.class))
                .collect(Collectors.toList());
    }

    public List<ProductoServicioDTO> searchByTipoAndNombre(String tipo, String nombreProducto) {
        log.info("Buscando {} con nombre que contenga: {}", tipo, nombreProducto);
        List<ProductoServicioEntity> items = psrepository.findByTipoAndNombreProductoContaining(tipo, nombreProducto);

        if (items.isEmpty()) {
            log.warn("No se encontraron {} con nombre que contenga: {}", tipo, nombreProducto);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("No se encontraron %s con ese nombre", tipo));
        }

        return items.stream()
                .map(entity -> modelMapper.map(entity, ProductoServicioDTO.class))
                .collect(Collectors.toList());
    }
}
