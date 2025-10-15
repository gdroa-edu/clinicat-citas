package com.clinicat.citas.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import clinicat.commons.entity.CitaEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICitasRepository extends IBaseRepository<CitaEntity, Long> {

    @Query("SELECT DISTINCT c FROM CitaEntity c " +
           "LEFT JOIN FETCH c.paciente p " +
           "LEFT JOIN FETCH c.usuario u " +
           "LEFT JOIN FETCH c.estado e " +
           "LEFT JOIN FETCH c.veterinario v " +
           "WHERE c.id = :id")
    Optional<CitaEntity> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT c FROM CitaEntity c " +
           "LEFT JOIN c.horario h " +
           "WHERE DATE(h.fecha) = :fecha")
    List<CitaEntity> findByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT c FROM CitaEntity c JOIN c.paciente p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<CitaEntity> findByPacienteNombre(@Param("nombre") String nombre);

    @Query("SELECT c FROM CitaEntity c JOIN c.paciente p JOIN p.usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<CitaEntity> findByPropietarioNombre(@Param("nombre") String nombre);

    @Query("SELECT c FROM CitaEntity c " +
           "LEFT JOIN c.veterinario v " +
           "WHERE LOWER(v.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(v.apellido) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<CitaEntity> findByVeterinarioNombre(@Param("searchTerm") String searchTerm);

    @Query("SELECT DISTINCT c FROM CitaEntity c " +
           "LEFT JOIN FETCH c.paciente p " +
           "LEFT JOIN FETCH c.usuario u " +
           "LEFT JOIN FETCH c.estado e " +
           "LEFT JOIN FETCH c.veterinario v " +
           "WHERE v.id = :veterinarioId")
    List<CitaEntity> findByVeterinarioId(@Param("veterinarioId") Long veterinarioId);

    @Query("SELECT DISTINCT c FROM CitaEntity c " +
           "LEFT JOIN FETCH c.paciente p " +
           "LEFT JOIN FETCH c.usuario u " +
           "LEFT JOIN FETCH c.estado e " +
           "LEFT JOIN FETCH c.veterinario v " +
           "WHERE e.id = :estadoId")
    List<CitaEntity> findByEstadoId(@Param("estadoId") Long estadoId);
}
