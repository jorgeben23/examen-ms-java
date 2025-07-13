package com.codigo.ms_auth.repository;

import com.codigo.ms_auth.entity.Rol;
import com.codigo.ms_auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombreRol (String nombreRol);
}
