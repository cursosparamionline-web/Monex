package com.example.Bknd_Categories.service;

import com.example.Bknd_Categories.dto.CategoryRequest;
import com.example.Bknd_Categories.dto.CategoryResponse;
import com.example.Bknd_Categories.entity.Category;
import com.example.Bknd_Categories.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

        @Mock
        private CategoryRepository categoryRepository;

        @InjectMocks
        private CategoryService categoryService;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("listAll: debe retornar categorías del usuario")
        void listAll_ok() {
                Category c1 = new Category();
                c1.setId(1L);
                c1.setName("Comida");
                c1.setDescription("Gastos de comida");
                c1.setCreatedByUserId(10L);

                Category c2 = new Category();
                c2.setId(2L);
                c2.setName("Transporte");
                c2.setDescription("Gastos de transporte");
                c2.setCreatedByUserId(10L);

                when(categoryRepository.findByCreatedByUserId(10L))
                                .thenReturn(List.of(c1, c2));

                List<CategoryResponse> result = categoryService.listAll(10L);

                assertEquals(2, result.size());
                assertEquals("Comida", result.get(0).getName());
                assertEquals("Transporte", result.get(1).getName());

                verify(categoryRepository).findByCreatedByUserId(10L);
        }

        @Test
        @DisplayName("getById: debe retornar categoría si pertenece al usuario")
        void getById_ok() {
                Category category = new Category();
                category.setId(1L);
                category.setName("Salud");
                category.setDescription("Gastos médicos");
                category.setCreatedByUserId(10L);

                when(categoryRepository.findByIdAndCreatedByUserId(1L, 10L))
                                .thenReturn(Optional.of(category));

                CategoryResponse result = categoryService.getById(1L, 10L);

                assertEquals(1L, result.getId());
                assertEquals("Salud", result.getName());
                assertEquals("Gastos médicos", result.getDescription());
                assertEquals(10L, result.getCreatedByUserId());

                verify(categoryRepository).findByIdAndCreatedByUserId(1L, 10L);
        }

        @Test
        @DisplayName("getById: debe lanzar excepción si no existe")
        void getById_noExiste() {
                when(categoryRepository.findByIdAndCreatedByUserId(99L, 10L))
                                .thenReturn(Optional.empty());

                NoSuchElementException ex = assertThrows(
                                NoSuchElementException.class,
                                () -> categoryService.getById(99L, 10L));

                assertEquals("Categoría no encontrada", ex.getMessage());
        }

        @Test
        @DisplayName("create: debe crear categoría normalizando nombre")
        void create_ok() {
                CategoryRequest request = new CategoryRequest("  Comida  ", "Gastos de comida");

                when(categoryRepository.existsByNameAndCreatedByUserId("Comida", 10L))
                                .thenReturn(false);

                when(categoryRepository.save(any(Category.class)))
                                .thenAnswer(inv -> {
                                        Category saved = inv.getArgument(0);
                                        saved.setId(1L);
                                        return saved;
                                });

                CategoryResponse result = categoryService.create(request, 10L);

                assertEquals(1L, result.getId());
                assertEquals("Comida", result.getName());
                assertEquals("Gastos de comida", result.getDescription());
                assertEquals(10L, result.getCreatedByUserId());

                verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("create: debe lanzar excepción si el nombre está vacío")
        void create_nombreVacio() {
                CategoryRequest request = new CategoryRequest("   ", "Sin nombre");

                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> categoryService.create(request, 10L));

                assertEquals("El nombre de la categoría es obligatorio", ex.getMessage());
                verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @DisplayName("create: debe lanzar excepción si categoría ya existe")
        void create_duplicada() {
                CategoryRequest request = new CategoryRequest("Comida", "Duplicada");

                when(categoryRepository.existsByNameAndCreatedByUserId("Comida", 10L))
                                .thenReturn(true);

                IllegalStateException ex = assertThrows(
                                IllegalStateException.class,
                                () -> categoryService.create(request, 10L));

                assertEquals("La categoría ya existe", ex.getMessage());
                verify(categoryRepository, never()).save(any(Category.class));
        }

        @Test
        @DisplayName("update: debe actualizar categoría existente")
        void update_ok() {
                Category existing = new Category();
                existing.setId(1L);
                existing.setName("Viejo");
                existing.setDescription("Descripción vieja");
                existing.setCreatedByUserId(10L);

                CategoryRequest request = new CategoryRequest("Nuevo", "Descripción nueva");

                when(categoryRepository.findByIdAndCreatedByUserId(1L, 10L))
                                .thenReturn(Optional.of(existing));

                when(categoryRepository.existsByNameAndCreatedByUserIdAndIdNot("Nuevo", 10L, 1L))
                                .thenReturn(false);

                when(categoryRepository.save(any(Category.class)))
                                .thenAnswer(inv -> inv.getArgument(0));

                CategoryResponse result = categoryService.update(1L, request, 10L);

                assertEquals("Nuevo", result.getName());
                assertEquals("Descripción nueva", result.getDescription());
                assertEquals(10L, result.getCreatedByUserId());

                verify(categoryRepository).save(existing);
        }

        @Test
        @DisplayName("delete: debe eliminar categoría existente")
        void delete_ok() {
                Category existing = new Category();
                existing.setId(1L);
                existing.setName("Comida");
                existing.setCreatedByUserId(10L);

                when(categoryRepository.findByIdAndCreatedByUserId(1L, 10L))
                                .thenReturn(Optional.of(existing));

                categoryService.delete(1L, 10L);

                verify(categoryRepository).delete(existing);
        }
}