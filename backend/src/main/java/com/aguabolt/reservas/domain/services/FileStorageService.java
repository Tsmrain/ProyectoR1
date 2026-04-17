package com.aguabolt.reservas.domain.services;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private Path root = Paths.get("uploads");

    public void setRootPath(Path path) {
        this.root = path;
    }

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de subidas", e);
        }
    }

    public String guardarArchivo(MultipartFile archivo) {
        try {
            String extension = "";
            String fileName = archivo.getOriginalFilename();
            if (fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf("."));
            }
            
            String nuevoNombre = UUID.randomUUID().toString() + extension;
            Files.copy(archivo.getInputStream(), this.root.resolve(nuevoNombre), StandardCopyOption.REPLACE_EXISTING);
            
            // Retorna la URL relativa para el frontend
            return "/api/archivos/" + nuevoNombre;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
        }
    }
}
