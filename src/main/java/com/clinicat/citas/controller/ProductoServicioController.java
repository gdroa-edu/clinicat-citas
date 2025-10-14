package com.clinicat.citas.controller;

import clinicat.commons.dto.ProductoServicioDTO;
import com.clinicat.citas.service.ProductosServiciosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productosservicios")
@Tag(name = "Productos y Servicios", description = "API para la gestión de productos y servicios veterinarios")
public class ProductoServicioController {
    private static final Logger log = LoggerFactory.getLogger(ProductoServicioController.class);

    @Autowired
    private ProductosServiciosService productosServiciosService;

    @GetMapping("/page/{page}")
    @Operation(summary = "Listar todos los productos y servicios paginados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "No se encontraron resultados")
    public ResponseEntity<Page<ProductoServicioDTO>> getAllProductosServicios(@PathVariable Integer page) {
        return ResponseEntity.ok(productosServiciosService.getAllProductosServicios(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto o servicio por ID")
    @ApiResponse(responseCode = "200", description = "Item encontrado exitosamente")
    @ApiResponse(responseCode = "404", description = "Item no encontrado")
    public ResponseEntity<ProductoServicioDTO> getProductoServicioById(@PathVariable Long id) {
        return ResponseEntity.ok(productosServiciosService.getProductoServicioById(id));
    }

    @GetMapping("/search/{nombre_producto}")
    @Operation(summary = "Buscar productos y servicios por nombre")
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    @ApiResponse(responseCode = "404", description = "No se encontraron resultados")
    public ResponseEntity<List<ProductoServicioDTO>> searchProductosServicios(@PathVariable String nombre_producto) {
        return ResponseEntity.ok(productosServiciosService.searchProductosServicios(nombre_producto));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(
        summary = "Obtener items por tipo",
        description = "Retorna todos los items de un tipo específico (PRODUCTO o SERVICIO)"
    )
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    @ApiResponse(responseCode = "404", description = "No se encontraron resultados")
    public ResponseEntity<List<ProductoServicioDTO>> getByTipo(
        @Parameter(description = "Tipo de item (PRODUCTO o SERVICIO)", example = "PRODUCTO")
        @PathVariable String tipo) {
        log.info("GET /api/productosservicios/tipo/{}", tipo);
        return ResponseEntity.ok(productosServiciosService.getByTipo(tipo));
    }

    @GetMapping("/tipo/{tipo}/search/{nombre}")
    @Operation(
        summary = "Buscar items por tipo y nombre",
        description = "Busca items de un tipo específico que contengan el texto proporcionado en su nombre"
    )
    @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    @ApiResponse(responseCode = "404", description = "No se encontraron resultados")
    public ResponseEntity<List<ProductoServicioDTO>> searchByTipoAndNombre(
        @Parameter(description = "Tipo de item (PRODUCTO o SERVICIO)", example = "PRODUCTO")
        @PathVariable String tipo,
        @Parameter(description = "Texto a buscar en el nombre", example = "vacuna")
        @PathVariable String nombre) {
        log.info("GET /api/productosservicios/tipo/{}/search/{}", tipo, nombre);
        return ResponseEntity.ok(productosServiciosService.searchByTipoAndNombre(tipo, nombre));
    }
}
