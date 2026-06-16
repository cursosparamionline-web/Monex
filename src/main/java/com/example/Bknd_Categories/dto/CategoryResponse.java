package com.example.Bknd_Categories.dto;

public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Long createdByUserId;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, String description, Long createdByUserId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdByUserId = createdByUserId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }
}