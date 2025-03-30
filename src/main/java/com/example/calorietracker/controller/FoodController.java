package com.example.calorietracker.controller;

import com.example.calorietracker.dto.FoodDto;
import com.example.calorietracker.service.FoodService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Блюда", description = "API для управления блюдами")
@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @Operation(summary = "Создание нового блюда",
            description = "Добавляет новое блюдо в базу данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Блюдо успешно создано",
                    content = @Content(schema = @Schema(implementation = FoodDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные блюда")
    })
    @PostMapping
    public ResponseEntity<FoodDto> createFood(
            @Parameter(description = "Данные блюда", required = true)
            @Valid @RequestBody FoodDto foodDto) {
        return new ResponseEntity<>(foodService.createFood(foodDto), HttpStatus.CREATED);
    }

    @Operation(summary = "Обновление блюда",
            description = "Обновляет информацию о блюде")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные блюда успешно обновлены",
                    content = @Content(schema = @Schema(implementation = FoodDto.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные блюда"),
            @ApiResponse(responseCode = "404", description = "Блюдо не найдено")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FoodDto> updateFood(
            @Parameter(description = "ID блюда", required = true, example = "1")
            @PathVariable("id") Long foodId,
            @Parameter(description = "Обновленные данные блюда", required = true)
            @Valid @RequestBody FoodDto foodDto) {
        return ResponseEntity.ok(foodService.updateFood(foodId, foodDto));
    }

    @Operation(summary = "Получение блюда по ID",
            description = "Возвращает информацию о блюде по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Блюдо найдено",
                    content = @Content(schema = @Schema(implementation = FoodDto.class))),
            @ApiResponse(responseCode = "404", description = "Блюдо не найдено")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FoodDto> getFoodById(
            @Parameter(description = "ID блюда", required = true, example = "1")
            @PathVariable("id") Long foodId) {
        return ResponseEntity.ok(foodService.getFoodById(foodId));
    }

    @Operation(summary = "Получение списка всех блюд",
            description = "Возвращает список всех доступных блюд")
    @ApiResponse(responseCode = "200", description = "Список блюд успешно получен",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FoodDto.class))))
    @GetMapping
    public ResponseEntity<List<FoodDto>> getAllFoods() {
        return ResponseEntity.ok(foodService.getAllFoods());
    }

    @Operation(summary = "Поиск блюд по названию",
            description = "Возвращает список блюд, названия которых содержат указанную строку")
    @ApiResponse(responseCode = "200", description = "Поиск выполнен успешно",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FoodDto.class))))
    @GetMapping("/search")
    public ResponseEntity<List<FoodDto>> searchFoodByName(
            @Parameter(description = "Строка для поиска в названиях блюд", required = true, example = "курин")
            @RequestParam("name") String name) {
        return ResponseEntity.ok(foodService.searchFoodByName(name));
    }

    @Operation(summary = "Удаление блюда",
            description = "Удаляет блюдо по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Блюдо успешно удалено"),
            @ApiResponse(responseCode = "404", description = "Блюдо не найдено")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(
            @Parameter(description = "ID блюда", required = true, example = "1")
            @PathVariable("id") Long foodId) {
        foodService.deleteFood(foodId);
        return ResponseEntity.noContent().build();
    }
}