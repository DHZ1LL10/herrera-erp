package com.herrera.erp.controller;

import com.herrera.erp.exception.ResourceNotFoundException;
import com.herrera.erp.model.PedidoImagen;
import com.herrera.erp.repository.PedidoImagenRepository;
import com.herrera.erp.repository.PedidoRepository;
import com.herrera.erp.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller de Imágenes
 */
@RestController
@RequestMapping("/api/imagenes")

@RequiredArgsConstructor
@Slf4j
public class ImagenController {

    private final CloudinaryService cloudinaryService;
    private final PedidoImagenRepository pedidoImagenRepository;
    private final PedidoRepository pedidoRepository;

    /**
     * POST /api/imagenes/upload
     * Subir imagen a Cloudinary
     */
    @PostMapping("/upload")
    public ResponseEntity<?> subirImagen(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String carpeta,
            @RequestParam(required = false) Long pedidoId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String descripcion) {

        try {
            if (!cloudinaryService.estaConfigurado()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error", "Cloudinary no está configurado"));
            }

            // Si no se especifica carpeta, usar "general"
            if (carpeta == null || carpeta.isEmpty()) {
                carpeta = "herrera-erp/general";
            }

            // Subir a Cloudinary
            CloudinaryService.CloudinaryUploadResult result = cloudinaryService.subirImagen(file, carpeta);

            // Si se especificó pedidoId, guardar la relación
            if (pedidoId != null) {
                var pedido = pedidoRepository.findById(pedidoId)
                        .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", pedidoId));

                PedidoImagen imagen = PedidoImagen.builder()
                        .pedido(pedido)
                        .nombreArchivo(file.getOriginalFilename())
                        .urlCloudinary(result.getUrl())
                        .publicIdCloudinary(result.getPublicId())
                        .tipo(tipo != null ? PedidoImagen.TipoImagen.valueOf(tipo.toUpperCase()) : null)
                        .descripcion(descripcion)
                        .esPrincipal(false)
                        .build();

                pedidoImagenRepository.save(imagen);
            }

            return ResponseEntity.ok(Map.of(
                    "url", result.getUrl(),
                    "publicId", result.getPublicId(),
                    "message", "Imagen subida exitosamente"));

        } catch (IOException e) {
            log.error("Error subiendo imagen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error subiendo imagen: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/imagenes/{id}
     * Eliminar imagen
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarImagen(@PathVariable Long id) {
        try {
            PedidoImagen imagen = pedidoImagenRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Imagen", "id", id));

            // Eliminar de Cloudinary
            if (cloudinaryService.estaConfigurado()) {
                cloudinaryService.eliminarImagen(imagen.getPublicIdCloudinary());
            }

            // Eliminar de BD
            pedidoImagenRepository.delete(imagen);

            return ResponseEntity.ok(Map.of("message", "Imagen eliminada exitosamente"));

        } catch (IOException e) {
            log.error("Error eliminando imagen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error eliminando imagen: " + e.getMessage()));
        }
    }

    /**
     * GET /api/imagenes/pedido/{pedidoId}
     * Listar imágenes de un pedido
     */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PedidoImagen>> listarImagenesPorPedido(@PathVariable Long pedidoId) {
        List<PedidoImagen> imagenes = pedidoImagenRepository.findByPedidoId(pedidoId);
        return ResponseEntity.ok(imagenes);
    }

    /**
     * GET /api/imagenes/configurado
     * Verificar si Cloudinary está configurado
     */
    @GetMapping("/configurado")
    public ResponseEntity<Map<String, Boolean>> verificarConfiguracion() {
        boolean configurado = cloudinaryService.estaConfigurado();
        return ResponseEntity.ok(Map.of("configurado", configurado));
    }
}
