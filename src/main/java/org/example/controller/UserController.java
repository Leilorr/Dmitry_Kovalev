package org.example.controller;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.exception.UserNotFoundException;
import org.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * REST контроллер для управления пользователями.
 * <p>
 * Предоставляет полный набор CRUD-операций с поддержкой HATEOAS.
 * Все методы возвращают данные в формате HAL+JSON со ссылками для навигации по API.
 * </p>
 *
 * <p><b>Типичный workflow:</b></p>
 * <ol>
 *   <li>Получить список пользователей (GET /api/users)</li>
 *   <li>Создать нового пользователя (POST /api/users)</li>
 *   <li>Обновить/удалить через ссылки в ресурсе пользователя</li>
 * </ol>
 *
 * @see UserService
 * @see UserRequestDto
 * @see UserResponseDto
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API для операций с пользователями")
public class UserController {

    private final UserService userService;

    /**
     * Возвращает список всех пользователей с HATEOAS-ссылками.
     * <p>
     * Каждый пользователь в списке содержит ссылки на свои операции (self, update, delete).
     * </p>
     *
     * @return коллекция пользователей в формате HAL+JSON
     */
    @GetMapping
    @Operation(summary = "Получить всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей")
    public CollectionModel<EntityModel<UserResponseDto>> getAllUsers() {
        List<EntityModel<UserResponseDto>> users = userService.getAllUsers().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel(),
                linkTo(methodOn(UserController.class).createUser(null)).withRel("create"));
    }

    /**
     * Возвращает пользователя по указанному ID.
     *
     * @param id идентификатор пользователя (должен существовать в БД)
     * @return ресурс пользователя с HATEOAS-ссылками
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID",
            description = "Возвращает данные конкретного пользователя")
    public EntityModel<UserResponseDto> getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        return toModel(userService.getUserById(id));
    }

    /**
     * Создает нового пользователя.
     *
     * @param userRequestDto DTO с данными нового пользователя
     * @return ResponseEntity с созданным ресурсом и Location header
     */
    @PostMapping
    @Operation(summary = "Создать пользователя",
            description = "Создает нового пользователя с предоставленными данными")
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания пользователя", required = true)
            @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        EntityModel<UserResponseDto> model = toModel(createdUser);

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).getUserById(createdUser.getId()).toUri())
                        .body(model));
    }

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param id идентификатор обновляемого пользователя
     * @param userRequestDto новые данные пользователя
     * @return обновленный ресурс пользователя
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя",
            description = "Обновляет данные существующего пользователя")
    public EntityModel<UserResponseDto> updateUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        return toModel(userService.updateUser(id, userRequestDto));
    }

    /**
     * Удаляет пользователя по указанному ID.
     *
     * @param id идентификатор удаляемого пользователя
     * @return ResponseEntity с кодом 204 (No Content)
     * @throws UserNotFoundException если пользователь с указанным ID не найден
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя",
            description = "Удаляет пользователя по указанному идентификатору")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Преобразует UserResponseDto в EntityModel с HATEOAS-ссылками.
     *
     * @param user DTO пользователя
     * @return EntityModel с данными и ссылками
     */
    private EntityModel<UserResponseDto> toModel(UserResponseDto user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }
}