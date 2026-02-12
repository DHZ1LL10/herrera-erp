package com.herrera.erp.repository;

import com.herrera.erp.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Roles
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/repository/RolRepository.java
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}