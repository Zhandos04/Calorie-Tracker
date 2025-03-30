package com.example.calorietracker.service;

import com.example.calorietracker.dto.FoodDto;
import com.example.calorietracker.dto.MealDto;
import com.example.calorietracker.dto.MealFoodDto;
import com.example.calorietracker.exception.InvalidDataException;
import com.example.calorietracker.exception.ResourceNotFoundException;
import com.example.calorietracker.model.*;
import com.example.calorietracker.repository.FoodRepository;
import com.example.calorietracker.repository.MealRepository;
import com.example.calorietracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private FoodService foodService;

    @InjectMocks
    private MealService mealService;

    private User user;
    private Food food1;
    private Food food2;
    private Meal meal;
    private MealDto mealDto;

    @BeforeEach
    void setUp() {
        // Настройка пользователя
        user = User.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .age(30)
                .weight(80.0)
                .height(180)
                .goal(Goal.WEIGHT_LOSS)
                .dailyCalorieTarget(2000)
                .build();

        // Настройка блюд
        food1 = Food.builder()
                .id(1L)
                .name("Куриная грудка")
                .caloriesPerServing(165)
                .proteins(31.0)
                .fats(3.6)
                .carbohydrates(0.0)
                .build();

        food2 = Food.builder()
                .id(2L)
                .name("Гречневая каша")
                .caloriesPerServing(132)
                .proteins(4.5)
                .fats(0.9)
                .carbohydrates(25.0)
                .build();

        // Настройка приема пищи
        meal = new Meal();
        meal.setId(1L);
        meal.setUser(user);
        meal.setMealDate(LocalDate.now());
        meal.setMealTime(LocalTime.of(12, 0));
        meal.setMealType("Обед");
        meal.setCreatedAt(LocalDateTime.now());

        // Добавление блюд в прием пищи
        MealFood mealFood1 = new MealFood();
        mealFood1.setId(1L);
        mealFood1.setMeal(meal);
        mealFood1.setFood(food1);
        mealFood1.setServings(1.0);

        MealFood mealFood2 = new MealFood();
        mealFood2.setId(2L);
        mealFood2.setMeal(meal);
        mealFood2.setFood(food2);
        mealFood2.setServings(1.5);

        meal.getMealFoods().add(mealFood1);
        meal.getMealFoods().add(mealFood2);

        // Настройка DTO
        MealFoodDto mealFoodDto1 = MealFoodDto.builder()
                .foodId(1L)
                .servings(1.0)
                .build();

        MealFoodDto mealFoodDto2 = MealFoodDto.builder()
                .foodId(2L)
                .servings(1.5)
                .build();

        mealDto = MealDto.builder()
                .id(1L)
                .userId(1L)
                .mealDate(LocalDate.now())
                .mealTime(LocalTime.of(12, 0))
                .mealType("Обед")
                .mealFoods(Arrays.asList(mealFoodDto1, mealFoodDto2))
                .build();
    }

    @Test
    void createMeal_WhenMealDtoIsValid_ReturnsMealDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(foodRepository.findById(1L)).thenReturn(Optional.of(food1));
        when(foodRepository.findById(2L)).thenReturn(Optional.of(food2));
        when(mealRepository.save(any(Meal.class))).thenReturn(meal);
        when(foodService.mapToDto(food1)).thenReturn(
                FoodDto.builder()
                        .id(1L)
                        .name("Куриная грудка")
                        .caloriesPerServing(165)
                        .proteins(31.0)
                        .fats(3.6)
                        .carbohydrates(0.0)
                        .build()
        );
        when(foodService.mapToDto(food2)).thenReturn(
                FoodDto.builder()
                        .id(2L)
                        .name("Гречневая каша")
                        .caloriesPerServing(132)
                        .proteins(4.5)
                        .fats(0.9)
                        .carbohydrates(25.0)
                        .build()
        );

        MealDto result = mealService.createMeal(mealDto);

        assertNotNull(result);
        assertEquals(mealDto.getMealDate(), result.getMealDate());
        assertEquals(mealDto.getMealTime(), result.getMealTime());
        assertEquals(mealDto.getMealType(), result.getMealType());
        assertNotNull(result.getMealFoods());
        assertEquals(2, result.getMealFoods().size());
        assertNotNull(result.getTotalCalories());
        assertNotNull(result.getTotalProteins());
        verify(mealRepository, times(1)).save(any(Meal.class));
    }

    @Test
    void createMeal_WhenUserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            mealService.createMeal(mealDto);
        });

        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void createMeal_WhenNoFoods_ThrowsInvalidDataException() {
        mealDto.setMealFoods(new ArrayList<>());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(InvalidDataException.class, () -> {
            mealService.createMeal(mealDto);
        });

        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    void getMealById_WhenMealExists_ReturnsMealDto() {
        when(mealRepository.findById(anyLong())).thenReturn(Optional.of(meal));
        when(foodService.mapToDto(any(Food.class))).thenReturn(
                FoodDto.builder()
                        .id(1L)
                        .name("Тестовое блюдо")
                        .caloriesPerServing(100)
                        .proteins(10.0)
                        .fats(5.0)
                        .carbohydrates(10.0)
                        .build()
        );

        MealDto result = mealService.getMealById(1L);

        assertNotNull(result);
        assertEquals(meal.getId(), result.getId());
        assertEquals(meal.getMealDate(), result.getMealDate());
        assertEquals(meal.getMealTime(), result.getMealTime());
        assertEquals(meal.getMealType(), result.getMealType());
    }

    @Test
    void getMealById_WhenMealDoesNotExist_ThrowsResourceNotFoundException() {
        when(mealRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            mealService.getMealById(1L);
        });
    }

    @Test
    void getUserMealsByDate_WhenUserExists_ReturnsListOfMealDto() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(mealRepository.findByUserIdAndMealDateOrderByMealTime(anyLong(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(meal));
        when(foodService.mapToDto(any(Food.class))).thenReturn(
                FoodDto.builder().build()
        );

        List<MealDto> result = mealService.getUserMealsByDate(1L, LocalDate.now());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(meal.getId(), result.get(0).getId());
    }

    @Test
    void getUserMealsByDate_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            mealService.getUserMealsByDate(1L, LocalDate.now());
        });
    }

    @Test
    void deleteMeal_WhenMealExists_DeletesMeal() {
        when(mealRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(mealRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> {
            mealService.deleteMeal(1L);
        });

        verify(mealRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteMeal_WhenMealDoesNotExist_ThrowsResourceNotFoundException() {
        when(mealRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            mealService.deleteMeal(1L);
        });

        verify(mealRepository, never()).deleteById(anyLong());
    }
}