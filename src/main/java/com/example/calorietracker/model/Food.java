package com.example.calorietracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "foods")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название блюда не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Количество калорий не может быть пустым")
    @Min(value = 0, message = "Количество калорий не может быть отрицательным")
    @Column(name = "calories_per_serving", nullable = false)
    private Integer caloriesPerServing;

    @NotNull(message = "Количество белков не может быть пустым")
    @Min(value = 0, message = "Количество белков не может быть отрицательным")
    @Column(name = "proteins", nullable = false)
    private Double proteins;

    @NotNull(message = "Количество жиров не может быть пустым")
    @Min(value = 0, message = "Количество жиров не может быть отрицательным")
    @Column(name = "fats", nullable = false)
    private Double fats;

    @NotNull(message = "Количество углеводов не может быть пустым")
    @Min(value = 0, message = "Количество углеводов не может быть отрицательным")
    @Column(name = "carbohydrates", nullable = false)
    private Double carbohydrates;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}