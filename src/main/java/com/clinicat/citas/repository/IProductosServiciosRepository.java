package com.clinicat.citas.repository;

import clinicat.commons.entity.ProductoServicioEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductosServiciosRepository extends IBaseRepository<ProductoServicioEntity, Long> {

    List<ProductoServicioEntity> findByNombreProductoContainingIgnoreCase(String nombreProducto);

    List<ProductoServicioEntity> findByTipoIgnoreCase(String tipo);

    @Query("SELECT p FROM ProductoServicioEntity p WHERE LOWER(p.tipo) = LOWER(:tipo) AND LOWER(p.nombreProducto) LIKE LOWER(CONCAT('%', :nombreProducto, '%'))")
    List<ProductoServicioEntity> findByTipoAndNombreProductoContaining(@Param("tipo") String tipo, @Param("nombreProducto") String nombreProducto);

}
