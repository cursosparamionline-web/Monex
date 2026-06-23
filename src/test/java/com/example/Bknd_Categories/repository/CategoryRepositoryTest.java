package com.example.Bknd_Categories.repository;

import com.example.Bknd_Categories.entity.Category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_repository",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=clave-test-1234567890-clave-test-1234567890",
        "jwt.expiration=86400000"
})
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category crearCategoria(String nombreBase, Long userId) {
        long timestamp = System.currentTimeMillis();

        Category category = new Category();
        category.setName(nombreBase + "_" + timestamp);
        category.setDescription("Descripción " + timestamp);
        category.setCreatedByUserId(userId);

        return category;
    }

    @Test
    @DisplayName("findByCreatedByUserId: debe retornar categorías del usuario")
    void findByCreatedByUserId_ok() {
        Long userId = 1000L + System.currentTimeMillis();

        Category category = crearCategoria("Comida", userId);
        categoryRepository.save(category);

        List<Category> result = categoryRepository.findByCreatedByUserId(userId);

        assertFalse(result.isEmpty());
        assertEquals(userId, result.get(0).getCreatedByUserId());
    }

    @Test
    @DisplayName("findByIdAndCreatedByUserId: debe encontrar categoría")
    void findByIdAndCreatedByUserId_ok() {
        Long userId = 2000L + System.currentTimeMillis();

        Category category = crearCategoria("Transporte", userId);
        Category saved = categoryRepository.save(category);

        Optional<Category> result =
                categoryRepository.findByIdAndCreatedByUserId(saved.getId(), userId);

        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    @DisplayName("existsByNameAndCreatedByUserId: debe retornar true")
    void existsByNameAndCreatedByUserId_true() {
        Long userId = 3000L + System.currentTimeMillis();

        Category category = crearCategoria("Salud", userId);
        Category saved = categoryRepository.save(category);

        boolean exists =
                categoryRepository.existsByNameAndCreatedByUserId(saved.getName(), userId);

        assertTrue(exists);
    }

    @Test
    @DisplayName("existsByNameAndCreatedByUserIdAndIdNot: debe retornar false para mismo ID")
    void existsByNameAndCreatedByUserIdAndIdNot_false() {
        Long userId = 4000L + System.currentTimeMillis();

        Category category = crearCategoria("Educacion", userId);
        Category saved = categoryRepository.save(category);

        boolean exists =
                categoryRepository.existsByNameAndCreatedByUserIdAndIdNot(
                        saved.getName(),
                        userId,
                        saved.getId()
                );

        assertFalse(exists);
    }
}