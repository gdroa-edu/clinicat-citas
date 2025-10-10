package com.clinicat.citas.repository;

import org.springframework.stereotype.Repository;
import clinicat.commons.entity.CitaDetalleEntity;
import java.util.List;

@Repository
public interface ICitaDetallesRepository extends IBaseRepository<CitaDetalleEntity, Long> {
    List<CitaDetalleEntity> findByCitaId(Long citaId);
}

