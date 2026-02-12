package com.herrera.erp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Servicio de Cloudinary
 * Gestión de subida y eliminación de imágenes
 */
@Service
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {

        if (cloudName.isEmpty() || apiKey.isEmpty() || apiSecret.isEmpty()) {
            log.warn("Cloudinary credentials not configured. Image upload will not work.");
            this.cloudinary = null;
        } else {
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret));
            log.info("Cloudinary configured successfully");
        }
    }

    /**
     * Subir imagen a Cloudinary
     * 
     * @param file    Archivo de imagen
     * @param carpeta Carpeta en Cloudinary (ej: "pedidos/2026-0001")
     * @return URL pública de la imagen y public_id
     */
    public CloudinaryUploadResult subirImagen(MultipartFile file, String carpeta) throws IOException {
        if (cloudinary == null) {
            throw new IllegalStateException("Cloudinary no está configurado");
        }

        log.info("Subiendo imagen a Cloudinary: {} bytes, carpeta: {}", file.getSize(), carpeta);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", carpeta,
                "resource_type", "auto");

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

        String url = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        log.info("Imagen subida exitosamente: {}", publicId);

        return new CloudinaryUploadResult(url, publicId);
    }

    /**
     * Eliminar imagen de Cloudinary
     * 
     * @param publicId ID público de la imagen
     */
    public void eliminarImagen(String publicId) throws IOException {
        if (cloudinary == null) {
            throw new IllegalStateException("Cloudinary no está configurado");
        }

        log.info("Eliminando imagen de Cloudinary: {}", publicId);

        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        log.info("Imagen eliminada exitosamente: {}", publicId);
    }

    /**
     * Eliminar múltiples imágenes por prefijo (carpeta)
     * 
     * @param carpeta Carpeta/prefijo de las imágenes
     */
    @SuppressWarnings("unchecked")
    public void eliminarImagenesPorCarpeta(String carpeta) throws Exception {
        if (cloudinary == null) {
            throw new IllegalStateException("Cloudinary no está configurado");
        }

        log.info("Eliminando imágenes de carpeta: {}", carpeta);

        Map<String, Object> params = ObjectUtils.asMap(
                "prefix", carpeta,
                "resource_type", "image");

        cloudinary.api().deleteResourcesByPrefix(carpeta, params);

        log.info("Imágenes eliminadas de carpeta: {}", carpeta);
    }

    /**
     * Verificar si Cloudinary está configurado
     */
    public boolean estaConfigurado() {
        return cloudinary != null;
    }

    /**
     * Resultado de subida a Cloudinary
     */
    public static class CloudinaryUploadResult {
        private final String url;
        private final String publicId;

        public CloudinaryUploadResult(String url, String publicId) {
            this.url = url;
            this.publicId = publicId;
        }

        public String getUrl() {
            return url;
        }

        public String getPublicId() {
            return publicId;
        }
    }
}
