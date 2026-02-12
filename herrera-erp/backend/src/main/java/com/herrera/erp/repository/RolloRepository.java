package com.herrera.erp.repository;

import com.herrera.erp.model.Rollo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Rollos
 * Ubicación:
 * backend/src/main/java/com/herrera/erp/repository/RolloRepository.java
 */
@Repository
public interface RolloRepository extends JpaRepository<Rollo, Long> {

    Optional<Rollo> findByCodigoRollo(String codigoRollo);

    List<Rollo> findByActivoTrue();

    List<Rollo> findByMaterialId(Long materialId);

    List<Rollo> findByDestino(Rollo.Destino destino);

    // Rollos disponibles (con metros > 0)
    @Query("SELECT r FROM Rollo r WHERE r.activo = true AND r.metrosActuales > 0")
    List<Rollo> findRollosDisponibles();

    // Rollos disponibles para corte
    @Query("SELECT r FROM Rollo r WHERE r.activo = true AND r.metrosActuales > 0 " +
            "AND (r.destino = 'CORTE' OR r.destino = 'MIXTO')")
    List<Rollo> findRollosDisponiblesParaCorte();

    // Rollos disponibles para venta
    @Query("SELECT r FROM Rollo r WHERE r.activo = true AND r.metrosActuales > 0 " +
            "AND (r.destino = 'VENTA' OR r.destino = 'MIXTO')")
    List<Rollo> findRollosDisponiblesParaVenta();

    boolean existsByCodigoRollo(String codigoRollo);
}