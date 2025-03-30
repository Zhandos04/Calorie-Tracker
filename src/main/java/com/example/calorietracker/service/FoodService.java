package com.example.calorietracker.service;

import com.example.calorietracker.dto.FoodDto;
import com.example.calorietracker.exception.InvalidDataException;
import com.example.calorietracker.exception.ResourceNotFoundException;
import com.example.calorietracker.model.Food;
import com.example.calorietracker.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {

    private final FoodRepository foodRepository;

    @Autowired
    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    /**
     * Создание нового блюда
     */
    @Transactional
    public FoodDto createFood(FoodDto foodDto) {
        foodRepository.findByNameIgnoreCase(foodDto.getName()).ifPresent(food -> {
            throw new InvalidDataException("Блюдо с названием '" + foodDto.getName() + "' уже существует");
        });

        Food food = mapToEntity(foodDto);
        Food savedFood = foodRepository.save(food);
        return mapToDto(savedFood);
    }

    /**
     * Обновление блюда
     */
    @Transactional
    public FoodDto updateFood(Long foodId, FoodDto foodDto) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Блюдо", "id", foodId));

        if (!food.getName().equalsIgnoreCase(foodDto.getName())) {
            foodRepository.findByNameIgnoreCase(foodDto.getName()).ifPresent(existingFood -> {
                throw new InvalidDataException("Блюдо с названием '" + foodDto.getName() + "' уже существует");
            });
        }

        food.setName(foodDto.getName());
        food.setCaloriesPerServing(foodDto.getCaloriesPerServing());
        food.setProteins(foodDto.getProteins());
        food.setFats(foodDto.getFats());
        food.setCarbohydrates(foodDto.getCarbohydrates());

        Food updatedFood = foodRepository.save(food);
        return mapToDto(updatedFood);
    }

    /**
     * Получение блюда по ID
     */
    public FoodDto getFoodById(Long foodId) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Блюдо", "id", foodId));
        return mapToDto(food);
    }

    /**
     * Получение списка всех блюд
     */
    public List<FoodDto> getAllFoods() {
        return foodRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск блюд по названию
     */
    public List<FoodDto> searchFoodByName(String name) {
        return foodRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Удаление блюда
     */
    @Transactional
    public void deleteFood(Long foodId) {
        if (!foodRepository.existsById(foodId)) {
            throw new ResourceNotFoundException("Блюдо", "id", foodId);
        }
        foodRepository.deleteById(foodId);
    }

    /**
     * Преобразование сущности в DTO
     */
    public FoodDto mapToDto(Food food) {
        return FoodDto.builder()
                .id(food.getId())
                .name(food.getName())
                .caloriesPerServing(food.getCaloriesPerServing())
                .proteins(food.getProteins())
                .fats(food.getFats())
                .carbohydrates(food.getCarbohydrates())
                .build();
    }

    /**
     * Преобразование DTO в сущность
     */
    private Food mapToEntity(FoodDto foodDto) {
        return Food.builder()
                .id(foodDto.getId())
                .name(foodDto.getName())
                .caloriesPerServing(foodDto.getCaloriesPerServing())
                .proteins(foodDto.getProteins())
                .fats(foodDto.getFats())
                .carbohydrates(foodDto.getCarbohydrates())
                .build();
    }
}