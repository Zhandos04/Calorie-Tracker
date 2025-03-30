package com.example.calorietracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Данные о приеме пищи")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealDto {

    @Schema(description = "Идентификатор приема пищи", example = "1")
    private Long id;

    @Schema(description = "Идентификатор пользователя", example = "1", required = true)
    private Long userId;

    @Schema(description = "Дата приема пищи", example = "2025-03-30", required = true)
    @NotNull(message = "Дата приема пищи не может быть пустой")
    @PastOrPresent(message = "Дата приема пищи не может быть в будущем")
    private LocalDate mealDate;

    @Schema(description = "Время приема пищи", example = "12:00:00", required = true)
    @NotNull(message = "Время приема пищи не может быть пустым")
    private LocalTime mealTime;

    @Schema(description = "Тип приема пищи", example = "Обед", required = true)
    @NotBlank(message = "Тип приема пищи не может быть пустым")
    private String mealType;

    @Schema(description = "Блюда в составе приема пищи", required = true)
    @Valid
    @Size(min = 1, message = "Прием пищи должен содержать хотя бы одно блюдо")
    private List<MealFoodDto> mealFoods = new ArrayList<>();

    // Рассчитанные поля
    @Schema(description = "Общее количество калорий", example = "750")
    private Integer totalCalories;

    @Schema(description = "Общее количество белков в граммах", example = "38.5")
    private Double totalProteins;

    @Schema(description = "Общее количество жиров в граммах", example = "15.2")
    private Double totalFats;

    @Schema(description = "Общее количество углеводов в граммах", example = "85.0")
    private Double totalCarbohydrates;

    public void calculateTotals() {
        this.totalCalories = mealFoods.stream().mapToInt(MealFoodDto::getCalories).sum();
        this.totalProteins = mealFoods.stream().mapToDouble(MealFoodDto::getProteins).sum();
        this.totalFats = mealFoods.stream().mapToDouble(MealFoodDto::getFats).sum();
        this.totalCarbohydrates = mealFoods.stream().mapToDouble(MealFoodDto::getCarbohydrates).sum();
    }
}