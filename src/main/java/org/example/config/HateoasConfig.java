package org.example.config;

import org.example.controller.UserController;
import org.example.dto.UserResponseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

/**
 * Конфигурация HATEOAS для автоматического добавления ссылок к ресурсам.
 */
@Configuration
public class HateoasConfig {

    /**
     * Создает assembler для пользовательских ресурсов.
     */
    @Bean
    public RepresentationModelAssembler<UserResponseDto, EntityModel<UserResponseDto>> userModelAssembler() {
        return new RepresentationModelAssembler<UserResponseDto, EntityModel<UserResponseDto>>() {
            @Override
            public EntityModel<UserResponseDto> toModel(UserResponseDto user) {
                return EntityModel.of(user,
                        WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(UserController.class)
                                        .getUserById(user.getId())
                        ).withSelfRel(),

                        WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(UserController.class)
                                        .updateUser(user.getId(), null)
                        ).withRel("update"),

                        WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(UserController.class)
                                        .deleteUser(user.getId())
                        ).withRel("delete"),

                        WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(UserController.class)
                                        .getAllUsers()
                        ).withRel("users"));
            }
        };
    }
}