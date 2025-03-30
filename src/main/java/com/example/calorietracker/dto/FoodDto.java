package com.example.calorietracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные о блюде")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto {

    @Schema(description = "Идентификатор блюда", example = "1")
    private Long id;

    @Schema(description = "Название блюда", example = "Куриная грудка", required = true)
    @NotBlank(message = "Название блюда не может быть пустым")
    @Size(min = 2, max = 100, message = "Название должно содержать от 2 до 100 символов")
    private String name;

    @Schema(description = "Количество калорий на порцию", example = "165", required = true, minimum = "0")
    @NotNull(message = "Количество калорий не может быть пустым")
    @Min(value = 0, message = "Количество калорий не может быть отрицательным")
    private Integer caloriesPerServing;

    @Schema(description = "Содержание белков на порцию в граммах", example = "31.0", required = true, minimum = "0")
    @NotNull(message = "Количество белков не может быть пустым")
    @Min(value = 0, message = "Количество белков не может быть отрицательным")
    private Double proteins;

    @Schema(description = "Содержание жиров на порцию в граммах", example = "3.6", required = true, minimum = "0")
    @NotNull(message = "Количество жиров не может быть пустым")
    @Min(value = 0, message = "Количество жиров не может быть отрицательным")
    private Double fats;

    @Schema(description = "Содержание углеводов на порцию в граммах", example = "0.0", required = true, minimum = "0")
    @NotNull(message = "Количество углеводов не может быть пустым")
    @Min(value = 0, message = "Количество углеводов не может быть отрицательным")
    private Double carbohydrates;
}