package com.example.Bknd_Categories.controllers;

import com.example.Bknd_Categories.dto.CategoryRequest;
import com.example.Bknd_Categories.dto.CategoryResponse;
import com.example.Bknd_Categories.service.CategoryService;
import com.example.Bknd_Categories.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "CRUD de categorias")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtService jwtService;

    public CategoryController(CategoryService categoryService, JwtService jwtService) {
        this.categoryService = categoryService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Listar categorias del usuario autenticado")
    @GetMapping
    public ResponseEntity<?> listAll(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.getUserIdFromAuthorizationHeader(authHeader);
            List<CategoryResponse> categories = categoryService.listAll(userId);
            return ResponseEntity.ok(categories);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @Operation(summary = "Listar categorías paginadas del usuario autenticado")
    @GetMapping("/paginadas")
    public ResponseEntity<?> listPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {

        try {
            Long userId = jwtService.getUserIdFromAuthorizationHeader(authHeader);

            Page<CategoryResponse> categories = categoryService.listAllPaged(
                    userId,
                    PageRequest.of(
                            page,
                            size,
                            Sort.by(Sort.Direction.ASC, "id")
                    )
            );

            return ResponseEntity.ok(categories);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    @Operation(summary = "Obtener categoria por ID del usuario autenticado")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.getUserIdFromAuthorizationHeader(authHeader);
            CategoryResponse category = categoryService.getById(id, userId);
            return ResponseEntity.ok(category);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear categoria")
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CategoryRequest request,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.getUserIdFromAuthorizationHeader(authHeader);
            CategoryResponse created = categoryService.create(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @Operation(summary = "Actualizar categoria")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.getUserIdFromAuthorizationHeader(authHeader);
            CategoryResponse updated = categoryService.update(id, request, userId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @Operation(summary = "Eliminar categoria")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.getUserIdFromAuthorizationHeader(authHeader);
            categoryService.delete(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (NoSuchElementException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar categorías de un usuario")
    @DeleteMapping("/admin/user/{userId}")
    public ResponseEntity<?> deleteCategoriesByUserId(@PathVariable Long userId) {
        categoryService.deleteCategoriesByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}