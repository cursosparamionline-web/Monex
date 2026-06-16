package com.example.Bknd_Categories.repository;

import com.example.Bknd_Categories.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByCreatedByUserId(Long createdByUserId);

    Page<Category> findByCreatedByUserId(Long createdByUserId, Pageable pageable);

    Optional<Category> findByIdAndCreatedByUserId(Long id, Long createdByUserId);

    boolean existsByNameAndCreatedByUserId(String name, Long createdByUserId);

    boolean existsByNameAndCreatedByUserIdAndIdNot(String name, Long createdByUserId, Long id);

    void deleteByCreatedByUserId(Long createdByUserId);
}