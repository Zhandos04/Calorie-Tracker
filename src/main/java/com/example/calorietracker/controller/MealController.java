package com.example.calorietracker.controller;

import com.example.calorietracker.dto.MealDto;
import com.example.calorietracker.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Приемы пищи", description = "API для управления приемами пищи")
@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    @Autowired
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @Operation(summary = "Создание нового приема пищи",
            description = "Создает новый прием пищи с указанными блюдами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Прием пищи успешно создан",
                    content = @Content(schema = @Schema(implementation = MealDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные приема пищи"),
            @ApiResponse(responseCode = "404", description = "Пользователь или блюдо не найдены")
    })
    @PostMapping
    public ResponseEntity<MealDto> createMeal(
            @Parameter(description = "Данные приема пищи", required = true)
            @Valid @RequestBody MealDto mealDto) {
        return new ResponseEntity<>(mealService.createMeal(mealDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновление приема пищи",
            description = "Обновляет информацию о приеме пищи и его составе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные приема пищи успешно обновлены",
                    content = @Content(schema = @Schema(implementation = MealDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные приема пищи"),
            @ApiResponse(responseCode = "404", description = "Прием пищи, пользователь или блюдо не найдены")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MealDto> updateMeal(
            @Parameter(description = "ID приема пищи", required = true, example = "1")
            @PathVariable("id") Long mealId,
            @Parameter(description = "Обновленные данные приема пищи", required = true)
            @Valid @RequestBody MealDto mealDto) {
        return ResponseEntity.ok(mealService.updateMeal(mealId, mealDto));
    }

    @Operation(summary = "Получение приема пищи по ID",
            description = "Возвращает информацию о приеме пищи по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Прием пищи найден",
                    content = @Content(schema = @Schema(implementation = MealDto.class))),
            @ApiResponse(responseCode = "404", description = "Прием пищи не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MealDto> getMealById(
            @Parameter(description = "ID приема пищи", required = true, example = "1")
            @PathVariable("id") Long mealId) {
        return ResponseEntity.ok(mealService.getMealById(mealId));
    }

    @Operation(summary = "Получение всех приемов пищи пользователя за день",
            description = "Возвращает список всех приемов пищи пользователя за указанную дату")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список приемов пищи успешно получен",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MealDto.class)))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<List<MealDto>> getUserMealsByDate(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Дата (YYYY-MM-DD)", required = true, example = "2025-03-30")
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(mealService.getUserMealsByDate(userId, date));
    }

    @Operation(summary = "Получение всех приемов пищи пользователя за период",
            description = "Возвращает список всех приемов пищи пользователя за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список приемов пищи успешно получен",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MealDto.class)))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user/{userId}/period")
    public ResponseEntity<List<MealDto>> getUserMealsByDateRange(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Дата начала периода (YYYY-MM-DD)", required = true, example = "2025-03-30")
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Дата окончания периода (YYYY-MM-DD)", required = true, example = "2025-03-31")
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(mealService.getUserMealsByDateRange(userId, startDate, endDate));
    }

    @Operation(summary = "Удаление приема пищи",
            description = "Удаляет прием пищи по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Прием пищи успешно удален"),
            @ApiResponse(responseCode = "404", description = "Прием пищи не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(
            @Parameter(description = "ID приема пищи", required = true, example = "1")
            @PathVariable("id") Long mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.noContent().build();
    }
}