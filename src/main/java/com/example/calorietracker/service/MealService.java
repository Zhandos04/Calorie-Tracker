package com.example.calorietracker.service;

import com.example.calorietracker.dto.FoodDto;
import com.example.calorietracker.dto.MealDto;
import com.example.calorietracker.dto.MealFoodDto;
import com.example.calorietracker.exception.InvalidDataException;
import com.example.calorietracker.exception.ResourceNotFoundException;
import com.example.calorietracker.model.Food;
import com.example.calorietracker.model.Meal;
import com.example.calorietracker.model.MealFood;
import com.example.calorietracker.model.User;
import com.example.calorietracker.repository.FoodRepository;
import com.example.calorietracker.repository.MealRepository;
import com.example.calorietracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final FoodService foodService;

    @Autowired
    public MealService(MealRepository mealRepository, UserRepository userRepository,
                       FoodRepository foodRepository, FoodService foodService) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.foodService = foodService;
    }

    /**
     * Создание нового приема пищи
     */
    @Transactional
    public MealDto createMeal(MealDto mealDto) {
        User user = userRepository.findById(mealDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", mealDto.getUserId()));

        // Проверка наличия блюд в приеме пищи
        if (mealDto.getMealFoods() == null || mealDto.getMealFoods().isEmpty()) {
            throw new InvalidDataException("Прием пищи должен содержать хотя бы одно блюдо");
        }

        // Создание сущности Meal
        Meal meal = Meal.builder()
                .user(user)
                .mealDate(mealDto.getMealDate())
                .mealTime(mealDto.getMealTime())
                .mealType(mealDto.getMealType())
                .mealFoods(new ArrayList<>())
                .build();

        // Добавление блюд в прием пищи
        for (MealFoodDto mealFoodDto : mealDto.getMealFoods()) {
            Food food = foodRepository.findById(mealFoodDto.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Блюдо", "id", mealFoodDto.getFoodId()));

            MealFood mealFood = MealFood.builder()
                    .meal(meal)
                    .food(food)
                    .servings(mealFoodDto.getServings())
                    .build();

            meal.addMealFood(mealFood);
        }

        // Сохранение приема пищи
        Meal savedMeal = mealRepository.save(meal);
        return mapToDto(savedMeal);
    }

    /**
     * Обновление приема пищи
     */
    @Transactional
    public MealDto updateMeal(Long mealId, MealDto mealDto) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Прием пищи", "id", mealId));

        // Проверка наличия блюд в приеме пищи
        if (mealDto.getMealFoods() == null || mealDto.getMealFoods().isEmpty()) {
            throw new InvalidDataException("Прием пищи должен содержать хотя бы одно блюдо");
        }

        // Обновление полей
        meal.setMealDate(mealDto.getMealDate());
        meal.setMealTime(mealDto.getMealTime());
        meal.setMealType(mealDto.getMealType());

        // Удаление существующих блюд
        meal.getMealFoods().clear();

        // Добавление блюд из DTO
        for (MealFoodDto mealFoodDto : mealDto.getMealFoods()) {
            Food food = foodRepository.findById(mealFoodDto.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Блюдо", "id", mealFoodDto.getFoodId()));

            MealFood mealFood = MealFood.builder()
                    .meal(meal)
                    .food(food)
                    .servings(mealFoodDto.getServings())
                    .build();

            meal.addMealFood(mealFood);
        }

        // Сохранение обновленного приема пищи
        Meal updatedMeal = mealRepository.save(meal);
        return mapToDto(updatedMeal);
    }

    /**
     * Получение приема пищи по ID
     */
    public MealDto getMealById(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Прием пищи", "id", mealId));
        return mapToDto(meal);
    }

    /**
     * Получение всех приемов пищи пользователя за день
     */
    public List<MealDto> getUserMealsByDate(Long userId, LocalDate date) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь", "id", userId);
        }

        List<Meal> meals = mealRepository.findByUserIdAndMealDateOrderByMealTime(userId, date);
        return meals.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение всех приемов пищи пользователя за период
     */
    public List<MealDto> getUserMealsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь", "id", userId);
        }

        List<Meal> meals = mealRepository.findByUserIdAndMealDateBetweenOrderByMealDateAscMealTimeAsc(
                userId, startDate, endDate);
        return meals.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Удаление приема пищи
     */
    @Transactional
    public void deleteMeal(Long mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new ResourceNotFoundException("Прием пищи", "id", mealId);
        }
        mealRepository.deleteById(mealId);
    }

    /**
     * Преобразование сущности в DTO
     */
    private MealDto mapToDto(Meal meal) {
        MealDto mealDto = MealDto.builder()
                .id(meal.getId())
                .userId(meal.getUser().getId())
                .mealDate(meal.getMealDate())
                .mealTime(meal.getMealTime())
                .mealType(meal.getMealType())
                .mealFoods(new ArrayList<>())
                .build();

        // Преобразование MealFood в MealFoodDto
        for (MealFood mealFood : meal.getMealFoods()) {
            MealFoodDto mealFoodDto = MealFoodDto.builder()
                    .id(mealFood.getId())
                    .foodId(mealFood.getFood().getId())
                    .foodName(mealFood.getFood().getName())
                    .servings(mealFood.getServings())
                    .build();

            // Расчет питательных веществ для данной порции
            FoodDto foodDto = foodService.mapToDto(mealFood.getFood());
            mealFoodDto.calculateNutrition(foodDto);

            mealDto.getMealFoods().add(mealFoodDto);
        }

        // Расчет общих питательных веществ для приема пищи
        mealDto.calculateTotals();

        return mealDto;
    }
}