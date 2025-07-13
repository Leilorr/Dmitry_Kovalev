package org.example.dto;

import lombok.Data;

@Data
public class UserEventDto {
    private String eventType; // "CREATED" или "DELETED"
    private String email;
    private String name;
}