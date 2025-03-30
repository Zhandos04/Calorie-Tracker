package com.example.calorietracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Дата приема пищи не может быть пустой")
    @PastOrPresent(message = "Дата приема пищи не может быть в будущем")
    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @NotNull(message = "Время приема пищи не может быть пустым")
    @Column(name = "meal_time", nullable = false)
    private LocalTime mealTime;

    @NotBlank(message = "Тип приема пищи не может быть пустым")
    @Column(name = "meal_type", nullable = false)
    private String mealType;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealFood> mealFoods = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    public int calculateTotalCalories() {
        return mealFoods.stream()
                .mapToInt(mealFood -> (int) (mealFood.getFood().getCaloriesPerServing() * mealFood.getServings()))
                .sum();
    }
    public void addMealFood(MealFood mealFood) {
        mealFoods.add(mealFood);
        mealFood.setMeal(this);
    }

    public void removeMealFood(MealFood mealFood) {
        mealFoods.remove(mealFood);
        mealFood.setMeal(null);
    }
}