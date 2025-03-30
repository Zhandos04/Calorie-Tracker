package com.example.calorietracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Данные о блюде в составе приема пищи")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodDto {

    @Schema(description = "Идентификатор записи", example = "1")
    private Long id;

    @Schema(description = "Идентификатор блюда", example = "1", required = true)
    @NotNull(message = "ID блюда не может быть пустым")
    private Long foodId;

    @Schema(description = "Название блюда", example = "Куриная грудка")
    private String foodName;

    @Schema(description = "Количество порций", example = "1.5", required = true, minimum = "0.1")
    @NotNull(message = "Количество порций не может быть пустым")
    @Positive(message = "Количество порций должно быть положительным числом")
    private Double servings;

    @Schema(description = "Количество калорий с учетом порций", example = "247")
    private Integer calories;

    @Schema(description = "Количество белков с учетом порций в граммах", example = "46.5")
    private Double proteins;

    @Schema(description = "Количество жиров с учетом порций в граммах", example = "5.4")
    private Double fats;

    @Schema(description = "Количество углеводов с учетом порций в граммах", example = "0.0")
    private Double carbohydrates;

    public void calculateNutrition(FoodDto food) {
        this.foodName = food.getName();
        this.calories = (int) (food.getCaloriesPerServing() * this.servings);
        this.proteins = food.getProteins() * this.servings;
        this.fats = food.getFats() * this.servings;
        this.carbohydrates = food.getCarbohydrates() * this.servings;
    }
}