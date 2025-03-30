package com.example.calorietracker.repository;

import com.example.calorietracker.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByUserIdAndMealDateOrderByMealTime(Long userId, LocalDate mealDate);
    List<Meal> findByUserIdAndMealDateBetweenOrderByMealDateAscMealTimeAsc(Long userId, LocalDate startDate, LocalDate endDate);
    @Query("SELECT DISTINCT m.mealDate FROM Meal m WHERE m.user.id = :userId ORDER BY m.mealDate")
    List<LocalDate> findDistinctMealDatesByUserIdOrderByMealDate(Long userId);
}