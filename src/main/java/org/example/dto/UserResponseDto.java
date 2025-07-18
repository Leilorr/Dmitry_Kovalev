package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

/**
 * DTO для ответа с данными пользователя.
 * Содержит данные пользователя и HATEOAS ссылки.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Модель данных пользователя для ответа")
public class UserResponseDto extends RepresentationModel<UserResponseDto> {

    /**
     * Уникальный идентификатор пользователя
     */
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;

    /**
     * Имя пользователя
     */
    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;

    /**
     * Email пользователя
     */
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;

    /**
     * Возраст пользователя
     */
    @Schema(description = "Возраст пользователя", example = "30")
    private Integer age;

    /**
     * Дата и время создания пользователя
     */
    @Schema(description = "Дата и время создания пользователя", example = "2023-05-15T10:00:00")
    private LocalDateTime createdAt;
}