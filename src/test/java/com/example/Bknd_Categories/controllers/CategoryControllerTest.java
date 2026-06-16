package com.example.Bknd_Categories.controllers;

import com.example.Bknd_Categories.dto.CategoryRequest;
import com.example.Bknd_Categories.dto.CategoryResponse;
import com.example.Bknd_Categories.service.CategoryService;
import com.example.Bknd_Categories.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    @DisplayName("GET /api/categorias retorna lista de categorías")
    void listAll_ok() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        when(categoryService.listAll(10L))
                .thenReturn(List.of(
                        new CategoryResponse(1L, "Comida", "Gastos de comida", 10L),
                        new CategoryResponse(2L, "Transporte", "Gastos de transporte", 10L)
                ));

        mockMvc.perform(get("/api/categorias")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Comida"))
                .andExpect(jsonPath("$[1].name").value("Transporte"));
    }

    @Test
    @DisplayName("GET /api/categorias/{id} retorna categoría por ID")
    void getById_ok() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        when(categoryService.getById(1L, 10L))
                .thenReturn(new CategoryResponse(1L, "Comida", "Gastos de comida", 10L));

        mockMvc.perform(get("/api/categorias/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Comida"))
                .andExpect(jsonPath("$.createdByUserId").value(10L));
    }

    @Test
    @DisplayName("GET /api/categorias/{id} retorna 404 si no existe")
    void getById_noExiste() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        when(categoryService.getById(99L, 10L))
                .thenThrow(new NoSuchElementException("Categoría no encontrada"));

        mockMvc.perform(get("/api/categorias/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/categorias crea categoría")
    void create_ok() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        when(categoryService.create(any(CategoryRequest.class), anyLong()))
                .thenReturn(new CategoryResponse(1L, "Comida", "Gastos de comida", 10L));

        String json = """
                {
                    "name": "Comida",
                    "description": "Gastos de comida"
                }
                """;

        mockMvc.perform(post("/api/categorias")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Comida"));
    }

    @Test
    @DisplayName("POST /api/categorias retorna 409 si categoría ya existe")
    void create_duplicada() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        when(categoryService.create(any(CategoryRequest.class), anyLong()))
                .thenThrow(new IllegalStateException("La categoría ya existe"));

        String json = """
                {
                    "name": "Comida",
                    "description": "Duplicada"
                }
                """;

        mockMvc.perform(post("/api/categorias")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().string("La categoría ya existe"));
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} actualiza categoría")
    void update_ok() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        when(categoryService.update(anyLong(), any(CategoryRequest.class), anyLong()))
                .thenReturn(new CategoryResponse(1L, "Salud", "Gastos médicos", 10L));

        String json = """
                {
                    "name": "Salud",
                    "description": "Gastos médicos"
                }
                """;

        mockMvc.perform(put("/api/categorias/1")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Salud"))
                .andExpect(jsonPath("$.description").value("Gastos médicos"));
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} elimina categoría")
    void delete_ok() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        doNothing().when(categoryService).delete(1L, 10L);

        mockMvc.perform(delete("/api/categorias/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} retorna 404 si no existe")
    void delete_noExiste() throws Exception {
        when(jwtService.getUserIdFromAuthorizationHeader("Bearer token")).thenReturn(10L);

        doThrow(new NoSuchElementException("Categoría no encontrada"))
                .when(categoryService).delete(99L, 10L);

        mockMvc.perform(delete("/api/categorias/99")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound());
    }
}