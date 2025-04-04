package com.example.calorietracker.repository;

import com.example.calorietracker.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<Food> findByNameIgnoreCase(String name);
    List<Food> findByNameContainingIgnoreCase(String name);
}