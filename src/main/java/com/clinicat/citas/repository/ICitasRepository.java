package com.clinicat.citas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import clinicat.commons.entity.CitaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICitasRepository extends IBaseRepository<CitaEntity, Long> {

    @Query("SELECT DISTINCT c FROM CitaEntity c " +
           "LEFT JOIN FETCH c.paciente p " +
           "LEFT JOIN FETCH c.usuario u " +
           "LEFT JOIN FETCH c.estado e " +
           "WHERE c.id = :id")
    Optional<CitaEntity> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT c FROM CitaEntity c " +
           "WHERE LOWER(c.usuario.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR CAST(c.fechaHora AS string) LIKE CONCAT('%', :searchTerm, '%')")
    List<CitaEntity> findByUsuarioOrFecha(@Param("searchTerm") String searchTerm);
}

