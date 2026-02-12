package com.herrera.erp.controller;

import com.herrera.erp.dto.LoginRequest;
import com.herrera.erp.dto.LoginResponse;
import com.herrera.erp.model.Usuario;
import com.herrera.erp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller de Autenticación
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login
     * Login de usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getUsername(), request.getPassword());
            Usuario usuario = authService.getUserFromToken(token);

            // Obtener nombres de permisos
            List<String> permisos = usuario.getRol().getPermisos().stream()
                    .map(p -> p.getModulo())
                    .collect(Collectors.toList());

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .message("Login exitoso")
                    .usuarioId(usuario.getId())
                    .username(usuario.getUsername())
                    .nombreCompleto(usuario.getNombreCompleto())
                    .rol(usuario.getRol().getNombre())
                    .permisos(permisos)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Credenciales inválidas");
            error.put("message", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    /**
     * POST /api/auth/validate
     * Validar token JWT
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        boolean isValid = authService.validateToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/logout
     * Logout (en frontend se elimina el token)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        return ResponseEntity.ok(response);
    }
}