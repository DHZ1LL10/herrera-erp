package com.herrera.erp.service;

import com.herrera.erp.exception.ResourceNotFoundException;
import com.herrera.erp.model.Rol;
import com.herrera.erp.model.Usuario;
import com.herrera.erp.repository.RolRepository;
import com.herrera.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de Usuarios
 * CRUD de usuarios del sistema
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtener todos los usuarios
     */
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtener solo usuarios activos
     */
    public List<Usuario> obtenerUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    /**
     * Obtener usuario por ID
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
    }

    /**
     * Obtener usuario por username
     */
    public Usuario obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
    }

    /**
     * Crear nuevo usuario
     */
    @Transactional
    public Usuario crearUsuario(String username, String password, String nombreCompleto,
            String email, String telefono, Long rolId) {
        log.info("Creando usuario: {}", username);

        // Verificar que no exista el username
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username");
        }

        // Obtener rol
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nombreCompleto(nombreCompleto)
                .email(email)
                .telefono(telefono)
                .rol(rol)
                .activo(true)
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("Usuario creado exitosamente: {}", username);

        return usuarioGuardado;
    }

    /**
     * Actualizar usuario existente
     */
    @Transactional
    public Usuario actualizarUsuario(Long id, String nombreCompleto, String email,
            String telefono, Long rolId) {
        log.info("Actualizando usuario ID: {}", id);

        Usuario usuario = obtenerUsuarioPorId(id);

        if (nombreCompleto != null)
            usuario.setNombreCompleto(nombreCompleto);
        if (email != null)
            usuario.setEmail(email);
        if (telefono != null)
            usuario.setTelefono(telefono);

        if (rolId != null) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));
            usuario.setRol(rol);
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado exitosamente: ID {}", id);

        return usuarioActualizado;
    }

    /**
     * Cambiar rol de un usuario
     */
    @Transactional
    public void cambiarRol(Long usuarioId, Long rolId) {
        log.info("Cambiando rol de usuario ID: {}", usuarioId);

        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", rolId));

        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        log.info("Rol actualizado para usuario: {}", usuario.getUsername());
    }

    /**
     * Activar/Desactivar usuario
     */
    @Transactional
    public void toggleActivoUsuario(Long id) {
        log.info("Cambiando estado activo de usuario ID: {}", id);

        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setActivo(!usuario.getActivo());
        usuarioRepository.save(usuario);

        log.info("Usuario {} {}", usuario.getUsername(),
                usuario.getActivo() ? "activado" : "desactivado");
    }

    /**
     * Verificar si existe username
     */
    public boolean existeUsername(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }

    /**
     * Contar usuarios activos
     */
    public long contarUsuariosActivos() {
        return usuarioRepository.countByActivoTrue();
    }
}
