package org.example.service;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import java.util.List;

public interface UserService {

    /**
     * Получает список всех пользователей.
     *
     * @return список DTO пользователей
     */
    List<UserResponseDto> getAllUsers();

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    UserResponseDto getUserById(Long id);

    /**
     * Создает нового пользователя.
     *
     * @param userRequestDto DTO с данными нового пользователя
     * @return DTO созданного пользователя
     * @throws IllegalArgumentException если email уже существует
     */
    UserResponseDto createUser(UserRequestDto userRequestDto);

    /**
     * Обновляет данные пользователя.
     *
     * @param id идентификатор пользователя
     * @param userRequestDto DTO с обновленными данными
     * @return DTO обновленного пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    void deleteUser(Long id);
}