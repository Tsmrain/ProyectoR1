package com.aguabolt.reservas.controllers;

import com.aguabolt.reservas.infrastructure.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HabitacionControllerMultimediaTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void testUploadPhotoWithAuth() throws Exception {
        String token = jwtTokenProvider.generateToken("admin@residencial.com", "ADMIN");
        
        MockMultipartFile file = new MockMultipartFile(
                "file", "room.jpg", "image/jpeg", "image data".getBytes());

        // ID 1 es creado por el DataInitializer
        mockMvc.perform(multipart("/api/habitaciones/1/fotos")
                .file(file)
                .param("esPrincipal", "true")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    void testUploadPhotoWithoutAuth() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "room.jpg", "image/jpeg", "image data".getBytes());

        mockMvc.perform(multipart("/api/habitaciones/1/fotos")
                .file(file)
                .param("esPrincipal", "true"))
                .andExpect(status().isForbidden());
    }
}
