package com.herrera.erp.repository;

import com.herrera.erp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Usuarios
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/repository/UsuarioRepository.java
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByRolId(Long rolId);

    Optional<Usuario> findByEmail(String email);
}