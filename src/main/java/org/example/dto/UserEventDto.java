package org.example.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserEventDto {
    private String eventType; // "CREATED" или "DELETED"
    private String email;
    private String name;
}