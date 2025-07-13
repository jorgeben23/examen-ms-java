package com.codigo.ms_ordenes.repository;

import com.codigo.ms_ordenes.entity.OrdenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdenRepository extends JpaRepository<OrdenEntity,Long> {

    List<OrdenEntity> findByUsuarioId(Long usuarioId);

    List<OrdenEntity> findByEstado(String estado);

    @Query("SELECT o FROM OrdenEntity o WHERE o.usuarioId = :usuarioId AND o.estado = :estado")
    List<OrdenEntity> findByUsuarioIdAndEstado(@Param("usuarioId") Long usuarioId,
                                         @Param("estado") String estado);

    @Query("SELECT o FROM OrdenEntity o ORDER BY o.fecha DESC")
    List<OrdenEntity> findAllByOrderByFechaDesc();

}
