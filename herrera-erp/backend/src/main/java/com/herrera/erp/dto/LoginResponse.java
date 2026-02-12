package com.herrera.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para respuesta de login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String type;
    private String message;

    // Información del usuario
    private Long usuarioId;
    private String username;
    private String nombreCompleto;
    private String rol;
    private List<String> permisos;
}
