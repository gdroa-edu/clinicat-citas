package com.clinicat.citas.repository;

import clinicat.commons.entity.EstadoCitaEntity;
import clinicat.commons.entity.RazaEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface IEstadosCitasRepository extends IBaseRepository<EstadoCitaEntity, Long> {
}
