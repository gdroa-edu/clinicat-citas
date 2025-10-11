package com.clinicat.citas.controller;

import clinicat.commons.dto.ProductoServicioDTO;
import com.clinicat.citas.service.ProductosServiciosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "ProductosServicios", description = "API para la gestión de productos y servicios que se realizan en la clinica")
public class ProductoServicioController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoServicioController.class);

    @Autowired
    private ProductosServiciosService psService;

    @GetMapping("/api/productosservicios/page/{page}")
    @Operation(summary = "Obtener todos los productos y servicios")
    @ApiResponse(responseCode = "200", description = "Lista de productos y servicios obtenida exitosamente")
    public ResponseEntity<Page<ProductoServicioDTO>> getAllProductosServicios(@PathVariable Integer page){
        logger.info("Solicitud para obtener la página {} de productos y servicios", page);
        return ResponseEntity.ok(psService.getAllProductosServicios(page));
    }

    @GetMapping("api/productosservicios/{id}")
    @Operation(summary = "Obtener producto o servicio por id")
    @ApiResponse(responseCode = "200", description = "Producto/Servicio obtenido exitosamente")
    public ResponseEntity<ProductoServicioDTO> getProductoServicioById(@PathVariable Long id) {
        logger.info("Solicitud para obtener el producto/servicio con ID: {}", id);
        return ResponseEntity.ok(psService.getProductoServicioById(id));
    }

    @GetMapping("/api/productosservicios/search/{nombre_producto}")
    public List<ProductoServicioDTO> searchProductosServicios(@PathVariable String nombre_producto) {
        logger.info("Solicitud de búsqueda de productos/servicios por nombre: {}", nombre_producto);
        return psService.searchProductosServicios(nombre_producto);
    }
}
