package com.example.calorietracker.service;

import com.example.calorietracker.dto.UserDto;
import com.example.calorietracker.exception.InvalidDataException;
import com.example.calorietracker.exception.ResourceNotFoundException;
import com.example.calorietracker.model.Goal;
import com.example.calorietracker.model.User;
import com.example.calorietracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создание нового пользователя
     */
    @Transactional
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new InvalidDataException("Пользователь с таким email уже существует");
        }
        Integer dailyCalorieTarget = calculateDailyCalorieTarget(
                userDto.getAge(),
                userDto.getWeight(),
                userDto.getHeight(),
                userDto.getGoal());
        userDto.setDailyCalorieTarget(dailyCalorieTarget);

        User user = mapToEntity(userDto);
        User savedUser = userRepository.save(user);

        return mapToDto(savedUser);
    }

    /**
     * Обновление данных пользователя
     */
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", userId));

        if (!user.getEmail().equals(userDto.getEmail()) && userRepository.existsByEmail(userDto.getEmail())) {
            throw new InvalidDataException("Пользователь с таким email уже существует");
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setAge(userDto.getAge());
        user.setWeight(userDto.getWeight());
        user.setHeight(userDto.getHeight());
        user.setGoal(userDto.getGoal());

        Integer dailyCalorieTarget = calculateDailyCalorieTarget(
                user.getAge(),
                user.getWeight(),
                user.getHeight(),
                user.getGoal());
        user.setDailyCalorieTarget(dailyCalorieTarget);

        User updatedUser = userRepository.save(user);
        return mapToDto(updatedUser);
    }

    /**
     * Получение пользователя по ID
     */
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", userId));
        return mapToDto(user);
    }

    /**
     * Получение списка всех пользователей
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Удаление пользователя
     */
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Пользователь", "id", userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Расчет дневной нормы калорий по формуле Харриса-Бенедикта
     */
    private Integer calculateDailyCalorieTarget(Integer age, Double weight, Integer height, Goal goal) {
        double bmr = 10 * weight + 6.25 * height - 5 * age + 5;

        double maintenanceCalories = bmr * 1.55;

        switch (goal) {
            case WEIGHT_LOSS:
                return (int) (maintenanceCalories * 0.8);
            case WEIGHT_GAIN:
                return (int) (maintenanceCalories * 1.15);
            case MAINTENANCE:
            default:
                return (int) maintenanceCalories;
        }
    }

    /**
     * Преобразование сущности в DTO
     */
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .weight(user.getWeight())
                .height(user.getHeight())
                .goal(user.getGoal())
                .dailyCalorieTarget(user.getDailyCalorieTarget())
                .build();
    }

    /**
     * Преобразование DTO в сущность
     */
    private User mapToEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .age(userDto.getAge())
                .weight(userDto.getWeight())
                .height(userDto.getHeight())
                .goal(userDto.getGoal())
                .dailyCalorieTarget(userDto.getDailyCalorieTarget())
                .build();
    }
}