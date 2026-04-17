package com.aguabolt.reservas.domain.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        fileStorageService.setRootPath(tempDir);
        fileStorageService.init();
    }

    @Test
    void testGuardarArchivo() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "image content".getBytes());

        String url = fileStorageService.guardarArchivo(file);

        assertNotNull(url);
        assertTrue(url.startsWith("/api/archivos/"));
        assertTrue(url.endsWith(".jpg"));
        
        String fileName = url.replace("/api/archivos/", "");
        assertTrue(Files.exists(tempDir.resolve(fileName)));
    }
}
