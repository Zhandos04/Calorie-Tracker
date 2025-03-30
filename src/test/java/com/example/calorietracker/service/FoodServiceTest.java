package com.example.calorietracker.service;

import com.example.calorietracker.dto.FoodDto;
import com.example.calorietracker.exception.InvalidDataException;
import com.example.calorietracker.exception.ResourceNotFoundException;
import com.example.calorietracker.model.Food;
import com.example.calorietracker.repository.FoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    private FoodDto foodDto;
    private Food food;

    @BeforeEach
    void setUp() {
        foodDto = FoodDto.builder()
                .id(1L)
                .name("Куриная грудка")
                .caloriesPerServing(165)
                .proteins(31.0)
                .fats(3.6)
                .carbohydrates(0.0)
                .build();

        food = Food.builder()
                .id(1L)
                .name("Куриная грудка")
                .caloriesPerServing(165)
                .proteins(31.0)
                .fats(3.6)
                .carbohydrates(0.0)
                .build();
    }

    @Test
    void createFood_WhenFoodDtoIsValid_ReturnsFoodDto() {
        when(foodRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(foodRepository.save(any(Food.class))).thenReturn(food);

        FoodDto result = foodService.createFood(foodDto);

        assertNotNull(result);
        assertEquals(foodDto.getName(), result.getName());
        assertEquals(foodDto.getCaloriesPerServing(), result.getCaloriesPerServing());
        assertEquals(foodDto.getProteins(), result.getProteins());
        verify(foodRepository, times(1)).save(any(Food.class));
    }

    @Test
    void createFood_WhenNameExists_ThrowsInvalidDataException() {
        when(foodRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(food));

        assertThrows(InvalidDataException.class, () -> {
            foodService.createFood(foodDto);
        });

        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void updateFood_WhenFoodExists_ReturnsUpdatedFoodDto() {
        Food updatedFood = Food.builder()
                .id(1L)
                .name("Куриная грудка отварная")
                .caloriesPerServing(150)
                .proteins(30.0)
                .fats(3.0)
                .carbohydrates(0.0)
                .build();

        FoodDto updatedFoodDto = FoodDto.builder()
                .id(1L)
                .name("Куриная грудка отварная")
                .caloriesPerServing(150)
                .proteins(30.0)
                .fats(3.0)
                .carbohydrates(0.0)
                .build();

        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));
        when(foodRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(foodRepository.save(any(Food.class))).thenReturn(updatedFood);

        FoodDto result = foodService.updateFood(1L, updatedFoodDto);

        assertNotNull(result);
        assertEquals(updatedFoodDto.getName(), result.getName());
        assertEquals(updatedFoodDto.getCaloriesPerServing(), result.getCaloriesPerServing());
        verify(foodRepository, times(1)).save(any(Food.class));
    }

    @Test
    void updateFood_WhenFoodDoesNotExist_ThrowsResourceNotFoundException() {
        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            foodService.updateFood(1L, foodDto);
        });

        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    void getFoodById_WhenFoodExists_ReturnsFoodDto() {
        when(foodRepository.findById(anyLong())).thenReturn(Optional.of(food));

        FoodDto result = foodService.getFoodById(1L);

        assertNotNull(result);
        assertEquals(food.getId(), result.getId());
        assertEquals(food.getName(), result.getName());
        assertEquals(food.getCaloriesPerServing(), result.getCaloriesPerServing());
    }

    @Test
    void getFoodById_WhenFoodDoesNotExist_ThrowsResourceNotFoundException() {
        when(foodRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            foodService.getFoodById(1L);
        });
    }

    @Test
    void getAllFoods_ReturnsListOfFoodDto() {
        Food food2 = Food.builder()
                .id(2L)
                .name("Гречневая каша")
                .caloriesPerServing(132)
                .proteins(4.5)
                .fats(0.9)
                .carbohydrates(25.0)
                .build();

        when(foodRepository.findAll()).thenReturn(Arrays.asList(food, food2));

        List<FoodDto> result = foodService.getAllFoods();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(food.getId(), result.get(0).getId());
        assertEquals(food2.getId(), result.get(1).getId());
    }

    @Test
    void searchFoodByName_ReturnsMatchingFoods() {
        Food food2 = Food.builder()
                .id(2L)
                .name("Куриный суп")
                .caloriesPerServing(120)
                .proteins(8.0)
                .fats(5.0)
                .carbohydrates(10.0)
                .build();

        when(foodRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(Arrays.asList(food, food2));

        List<FoodDto> result = foodService.searchFoodByName("кур");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getName().toLowerCase().contains("кур"));
        assertTrue(result.get(1).getName().toLowerCase().contains("кур"));
    }

    @Test
    void deleteFood_WhenFoodExists_DeletesFood() {
        when(foodRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(foodRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> {
            foodService.deleteFood(1L);
        });

        verify(foodRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteFood_WhenFoodDoesNotExist_ThrowsResourceNotFoundException() {
        when(foodRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            foodService.deleteFood(1L);
        });

        verify(foodRepository, never()).deleteById(anyLong());
    }
}