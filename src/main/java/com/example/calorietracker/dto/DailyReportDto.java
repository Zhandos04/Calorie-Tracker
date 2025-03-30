package com.example.calorietracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Отчет о питании за день")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportDto {

    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long userId;

    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String userName;

    @Schema(description = "Дата отчета", example = "2025-03-30")
    private LocalDate date;

    @Schema(description = "Дневная норма калорий", example = "2100")
    private Integer dailyCalorieTarget;

    @Schema(description = "Общее количество потребленных калорий", example = "1850")
    private Integer totalCaloriesConsumed;

    @Schema(description = "Общее количество потребленных белков в граммах", example = "96.5")
    private Double totalProteinsConsumed;

    @Schema(description = "Общее количество потребленных жиров в граммах", example = "45.2")
    private Double totalFatsConsumed;

    @Schema(description = "Общее количество потребленных углеводов в граммах", example = "180.0")
    private Double totalCarbohydratesConsumed;

    @Schema(description = "Признак соответствия дневной нормы калорий", example = "true")
    private Boolean withinCalorieTarget;

    @Schema(description = "Дефицит калорий (отрицательное значение означает избыток)", example = "250")
    private Integer calorieDeficit;

    @Schema(description = "Список приемов пищи за день")
    private List<MealDto> meals = new ArrayList<>();

    public void calculateTotals() {
        this.totalCaloriesConsumed = meals.stream().mapToInt(MealDto::getTotalCalories).sum();
        this.totalProteinsConsumed = meals.stream().mapToDouble(MealDto::getTotalProteins).sum();
        this.totalFatsConsumed = meals.stream().mapToDouble(MealDto::getTotalFats).sum();
        this.totalCarbohydratesConsumed = meals.stream().mapToDouble(MealDto::getTotalCarbohydrates).sum();

        this.withinCalorieTarget = this.totalCaloriesConsumed <= this.dailyCalorieTarget;
        this.calorieDeficit = this.dailyCalorieTarget - this.totalCaloriesConsumed;
    }
}