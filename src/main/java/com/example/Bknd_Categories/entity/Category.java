package com.example.Bknd_Categories.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_category_user_name",
                columnNames = {"created_by_user_id", "name"}
        )
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    //NUEVO CAMPO
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //GETTER NUEVO
    public String getDescription() {
        return description;
    }

    //SETTER NUEVO
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}