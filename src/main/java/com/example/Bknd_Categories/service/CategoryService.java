package com.example.Bknd_Categories.service;

import com.example.Bknd_Categories.dto.CategoryRequest;
import com.example.Bknd_Categories.dto.CategoryResponse;
import com.example.Bknd_Categories.entity.Category;
import com.example.Bknd_Categories.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> listAll(Long userId) {
        return categoryRepository.findByCreatedByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Page<CategoryResponse> listAllPaged(Long userId, Pageable pageable) {
        return categoryRepository.findByCreatedByUserId(userId, pageable)
                .map(this::toResponse);
    }

    public CategoryResponse getById(Long id, Long userId) {
        Category category = categoryRepository.findByIdAndCreatedByUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));

        return toResponse(category);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request, Long userId) {
        String normalizedName = normalizeName(request.getName());

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        if (categoryRepository.existsByNameAndCreatedByUserId(normalizedName, userId)) {
            throw new IllegalStateException("La categoría ya existe");
        }

        Category category = new Category();
        category.setName(normalizedName);
        category.setDescription(request.getDescription());
        category.setCreatedByUserId(userId);

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request, Long userId) {
        Category existing = categoryRepository.findByIdAndCreatedByUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));

        String normalizedName = normalizeName(request.getName());

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio");
        }

        if (categoryRepository.existsByNameAndCreatedByUserIdAndIdNot(normalizedName, userId, id)) {
            throw new IllegalStateException("La categoría ya existe");
        }

        existing.setName(normalizedName);
        existing.setDescription(request.getDescription());

        return toResponse(categoryRepository.save(existing));
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Category existing = categoryRepository.findByIdAndCreatedByUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada"));

        categoryRepository.delete(existing);
    }

    @Transactional
    public void deleteCategoriesByUserId(Long userId) {
        categoryRepository.deleteByCreatedByUserId(userId);
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedByUserId()
        );
    }
}