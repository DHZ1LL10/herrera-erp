package com.herrera.erp.service;

import com.herrera.erp.model.Usuario;
import com.herrera.erp.repository.UsuarioRepository;
import com.herrera.erp.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de Autenticación
 * Ubicación: backend/src/main/java/com/herrera/erp/service/AuthService.java
 */
@Service
@Slf4j
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // Constructor manual para que @Lazy funcione correctamente
    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            @Lazy AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Login de usuario - Genera JWT
     */
    @Transactional
    public String login(String username, String password) {
        log.info("Intento de login para usuario: {}", username);

        // Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        // Obtener usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Actualizar último login
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Generar JWT
        String token = jwtUtil.generateToken(usuario);

        log.info("Login exitoso para usuario: {}", username);
        return token;
    }

    /**
     * Validar token JWT
     */
    public boolean validateToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = loadUserByUsername(username);
            return jwtUtil.validateToken(token, userDetails);
        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtener usuario desde token
     */
    public Usuario getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token);
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    /**
     * Implementación de UserDetailsService (Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
    }

    /**
     * Cambiar contraseña
     */
    @Transactional
    public void cambiarPassword(Long usuarioId, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar password actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        // Actualizar password
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);

        log.info("Contraseña actualizada para usuario: {}", usuario.getUsername());
    }

    /**
     * Verificar si usuario tiene permiso
     */
    public boolean tienePermiso(Usuario usuario, String modulo, String accion) {
        if (usuario.getRol() == null) {
            return false;
        }

        return usuario.getRol().getPermisos().stream()
                .filter(p -> p.getModulo().equalsIgnoreCase(modulo))
                .findFirst()
                .map(permiso -> {
                    return switch (accion.toLowerCase()) {
                        case "crear" -> permiso.getPuedeCrear();
                        case "leer" -> permiso.getPuedeLeer();
                        case "editar" -> permiso.getPuedeEditar();
                        case "eliminar" -> permiso.getPuedeEliminar();
                        default -> false;
                    };
                })
                .orElse(false);
    }
}