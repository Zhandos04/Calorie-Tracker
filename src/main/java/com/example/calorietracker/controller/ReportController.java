package com.example.calorietracker.controller;

import com.example.calorietracker.dto.DailyReportDto;
import com.example.calorietracker.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Отчеты", description = "API для получения отчетов о питании")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Получение отчета о питании за день",
            description = "Возвращает детальный отчет о питании пользователя за указанную дату")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отчет успешно получен",
                    content = @Content(schema = @Schema(implementation = DailyReportDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/daily/{userId}/{date}")
    public ResponseEntity<DailyReportDto> getDailyReport(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Дата (YYYY-MM-DD)", required = true, example = "2025-03-30")
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyReport(userId, date));
    }

    @Operation(summary = "Проверка соответствия дневной нормы калорий",
            description = "Проверяет, уложился ли пользователь в свою дневную норму калорий за указанную дату")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Проверка выполнена успешно"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/calorie-check/{userId}/{date}")
    public ResponseEntity<Map<String, Object>> checkCalorieTarget(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Дата (YYYY-MM-DD)", required = true, example = "2025-03-30")
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        DailyReportDto report = reportService.getDailyReport(userId, date);

        Map<String, Object> response = Map.of(
                "userId", userId,
                "date", date,
                "dailyCalorieTarget", report.getDailyCalorieTarget(),
                "caloriesConsumed", report.getTotalCaloriesConsumed(),
                "withinTarget", report.getWithinCalorieTarget(),
                "calorieDeficit", report.getCalorieDeficit()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получение истории питания по дням за указанный период",
            description = "Возвращает историю питания пользователя по дням за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "История питания успешно получена",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DailyReportDto.class)))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<DailyReportDto>> getFeedingHistory(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable("userId") Long userId,
            @Parameter(description = "Дата начала периода (YYYY-MM-DD)", required = true, example = "2025-03-30")
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Дата окончания периода (YYYY-MM-DD)", required = true, example = "2025-03-31")
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getFeedingHistoryByDateRange(userId, startDate, endDate));
    }

    @Operation(summary = "Получение дат, в которые пользователь принимал пищу",
            description = "Возвращает список дат, в которые пользователь принимал пищу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список дат успешно получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/meal-dates/{userId}")
    public ResponseEntity<List<LocalDate>> getUserMealDates(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(reportService.getUserMealDates(userId));
    }
}