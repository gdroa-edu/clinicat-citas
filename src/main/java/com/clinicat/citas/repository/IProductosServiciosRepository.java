package com.clinicat.citas.repository;

import clinicat.commons.entity.ProductoServicioEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductosServiciosRepository extends IBaseRepository<ProductoServicioEntity, Long> {

    List<ProductoServicioEntity> findByNombreProductoContainingIgnoreCase(String nombre_producto);

}
