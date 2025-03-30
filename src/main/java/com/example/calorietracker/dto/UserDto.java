package com.example.calorietracker.dto;

import com.example.calorietracker.model.Goal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные пользователя")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Schema(description = "Идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов", required = true)
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно содержать от 2 до 100 символов")
    private String name;

    @Schema(description = "Email пользователя", example = "ivan@example.com", required = true)
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(description = "Возраст пользователя", example = "30", required = true, minimum = "1", maximum = "120")
    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 1, message = "Возраст должен быть положительным числом")
    @Max(value = 120, message = "Возраст не может превышать 120 лет")
    private Integer age;

    @Schema(description = "Вес пользователя в кг", example = "80.5", required = true, minimum = "1")
    @NotNull(message = "Вес не может быть пустым")
    @Positive(message = "Вес должен быть положительным числом")
    private Double weight;

    @Schema(description = "Рост пользователя в см", example = "180", required = true, minimum = "50", maximum = "250")
    @NotNull(message = "Рост не может быть пустым")
    @Min(value = 50, message = "Рост должен быть не менее 50 см")
    @Max(value = 250, message = "Рост не может превышать 250 см")
    private Integer height;

    @Schema(description = "Цель пользователя", example = "WEIGHT_LOSS", required = true,
            allowableValues = {"WEIGHT_LOSS", "MAINTENANCE", "WEIGHT_GAIN"})
    @NotNull(message = "Цель не может быть пустой")
    private Goal goal;

    @Schema(description = "Дневная норма калорий (рассчитывается автоматически)", example = "2100")
    private Integer dailyCalorieTarget;
}