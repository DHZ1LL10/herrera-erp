package com.herrera.erp.controller;

import com.herrera.erp.model.Usuario;
import com.herrera.erp.service.UsuarioService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de Usuarios
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * GET /api/usuarios
     * Listar todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios(
            @RequestParam(required = false, defaultValue = "false") boolean soloActivos) {

        List<Usuario> usuarios = soloActivos
                ? usuarioService.obtenerUsuariosActivos()
                : usuarioService.obtenerTodosUsuarios();

        return ResponseEntity.ok(usuarios);
    }

    /**
     * GET /api/usuarios/{id}
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * POST /api/usuarios
     * Crear nuevo usuario
     */
    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody CrearUsuarioRequest request) {
        try {
            Usuario usuario = usuarioService.crearUsuario(
                    request.getUsername(),
                    request.getPassword(),
                    request.getNombreCompleto(),
                    request.getEmail(),
                    request.getTelefono(),
                    request.getRolId());

            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualizar usuario existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody ActualizarUsuarioRequest request) {

        Usuario usuario = usuarioService.actualizarUsuario(
                id,
                request.getNombreCompleto(),
                request.getEmail(),
                request.getTelefono(),
                request.getRolId());

        return ResponseEntity.ok(usuario);
    }

    /**
     * PUT /api/usuarios/{id}/toggle
     * Activar/Desactivar usuario
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Map<String, String>> toggleUsuario(@PathVariable Long id) {
        usuarioService.toggleActivoUsuario(id);
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);

        String mensaje = usuario.getActivo()
                ? "Usuario activado correctamente"
                : "Usuario desactivado correctamente";

        return ResponseEntity.ok(Map.of(
                "message", mensaje,
                "activo", usuario.getActivo().toString()));
    }

    /**
     * PUT /api/usuarios/{id}/rol
     * Cambiar rol de usuario
     */
    @PutMapping("/{id}/rol")
    public ResponseEntity<Map<String, String>> cambiarRol(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {

        Long rolId = request.get("rolId");
        usuarioService.cambiarRol(id, rolId);

        return ResponseEntity.ok(Map.of("message", "Rol actualizado correctamente"));
    }

    /**
     * GET /api/usuarios/existe/{username}
     * Verificar si existe username
     */
    @GetMapping("/existe/{username}")
    public ResponseEntity<Map<String, Boolean>> existeUsername(@PathVariable String username) {
        boolean existe = usuarioService.existeUsername(username);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    // DTOs internos
    @Data
    static class CrearUsuarioRequest {
        private String username;
        private String password;
        private String nombreCompleto;
        private String email;
        private String telefono;
        private Long rolId;
    }

    @Data
    static class ActualizarUsuarioRequest {
        private String nombreCompleto;
        private String email;
        private String telefono;
        private Long rolId;
    }
}
