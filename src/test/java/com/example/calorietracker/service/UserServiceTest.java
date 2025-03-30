package com.example.calorietracker.service;

import com.example.calorietracker.dto.UserDto;
import com.example.calorietracker.exception.InvalidDataException;
import com.example.calorietracker.exception.ResourceNotFoundException;
import com.example.calorietracker.model.Goal;
import com.example.calorietracker.model.User;
import com.example.calorietracker.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .age(30)
                .weight(80.0)
                .height(180)
                .goal(Goal.WEIGHT_LOSS)
                .build();

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
    }

    @Test
    void createUser_WhenUserDtoIsValid_ReturnsUserDto() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertNotNull(result.getDailyCalorieTarget());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WhenEmailExists_ThrowsInvalidDataException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(InvalidDataException.class, () -> {
            userService.createUser(userDto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserExists_ReturnsUpdatedUserDto() {
        User updatedUser = User.builder()
                .id(1L)
                .name("Иван Иванов Обновленный")
                .email("ivan@example.com")
                .age(31)
                .weight(78.0)
                .height(180)
                .goal(Goal.WEIGHT_LOSS)
                .dailyCalorieTarget(1900)
                .build();

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Иван Иванов Обновленный")
                .email("ivan@example.com")
                .age(31)
                .weight(78.0)
                .height(180)
                .goal(Goal.WEIGHT_LOSS)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updatedUserDto);

        assertNotNull(result);
        assertEquals(updatedUserDto.getName(), result.getName());
        assertEquals(updatedUserDto.getAge(), result.getAge());
        assertEquals(updatedUserDto.getWeight(), result.getWeight());
        assertNotNull(result.getDailyCalorieTarget());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(1L, userDto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_WhenUserExists_ReturnsUserDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    void getAllUsers_ReturnsListOfUserDto() {
        User user2 = User.builder()
                .id(2L)
                .name("Петр Петров")
                .email("petr@example.com")
                .age(25)
                .weight(70.0)
                .height(175)
                .goal(Goal.MAINTENANCE)
                .dailyCalorieTarget(2200)
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        assertEquals(user2.getId(), result.get(1).getId());
    }

    @Test
    void deleteUser_WhenUserExists_DeletesUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> {
            userService.deleteUser(1L);
        });

        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });

        verify(userRepository, never()).deleteById(anyLong());
    }
}