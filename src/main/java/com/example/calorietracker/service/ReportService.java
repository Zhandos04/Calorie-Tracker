package com.example.calorietracker.service;

import com.example.calorietracker.dto.DailyReportDto;
import com.example.calorietracker.dto.MealDto;
import com.example.calorietracker.exception.ResourceNotFoundException;
import com.example.calorietracker.model.User;
import com.example.calorietracker.repository.MealRepository;
import com.example.calorietracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final MealService mealService;

    @Autowired
    public ReportService(UserRepository userRepository, MealRepository mealRepository, MealService mealService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.mealService = mealService;
    }

    /**
     * Создание отчета о питании за день
     */
    public DailyReportDto getDailyReport(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", userId));

        List<MealDto> meals = mealService.getUserMealsByDate(userId, date);

        DailyReportDto report = DailyReportDto.builder()
                .userId(user.getId())
                .userName(user.getName())
                .date(date)
                .dailyCalorieTarget(user.getDailyCalorieTarget())
                .meals(meals)
                .build();

        // Расчет общих питательных веществ и проверка соответствия норме калорий
        report.calculateTotals();

        return report;
    }

    /**
     * Проверка соответствия дневной нормы калорий
     */
    public boolean isWithinCalorieTarget(Long userId, LocalDate date) {
        DailyReportDto report = getDailyReport(userId, date);
        return report.getWithinCalorieTarget();
    }

    /**
     * Получение истории питания по дням за указанный период
     */
    public List<DailyReportDto> getFeedingHistoryByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", userId));

        // Проверка корректности диапазона дат
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
        }

        // Ограничение максимального периода (например, 31 день)
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (daysBetween > 31) {
            throw new IllegalArgumentException("Максимальный период для отчета - 31 день");
        }

        List<DailyReportDto> history = new ArrayList<>();

        // Получение всех приемов пищи за период
        List<MealDto> allMeals = mealService.getUserMealsByDateRange(userId, startDate, endDate);

        // Группировка приемов пищи по датам
        Map<LocalDate, List<MealDto>> mealsByDate = new HashMap<>();
        for (MealDto meal : allMeals) {
            mealsByDate.computeIfAbsent(meal.getMealDate(), k -> new ArrayList<>()).add(meal);
        }

        // Создание отчетов для каждого дня
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<MealDto> dailyMeals = mealsByDate.getOrDefault(date, new ArrayList<>());

            DailyReportDto dailyReport = DailyReportDto.builder()
                    .userId(user.getId())
                    .userName(user.getName())
                    .date(date)
                    .dailyCalorieTarget(user.getDailyCalorieTarget())
                    .meals(dailyMeals)
                    .build();

            dailyReport.calculateTotals();
            history.add(dailyReport);
        }

        return history;
    }

    /**
     * Получение дат, в которые пользователь принимал пищу
     */
    public List<LocalDate> getUserMealDates(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь", "id", userId);
        }
        return mealRepository.findDistinctMealDatesByUserIdOrderByMealDate(userId);
    }
}