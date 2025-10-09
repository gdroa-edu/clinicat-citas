package com.clinicat.citas.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import clinicat.commons.entity.PacienteEntity;
import java.util.List;

@Repository
public interface IPacientesRepository extends IBaseRepository<PacienteEntity, Long> {
    @Query("SELECT p FROM PacienteEntity p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<PacienteEntity> findByNombreOrUsuarioNombreContaining(@Param("searchTerm") String searchTerm);
}