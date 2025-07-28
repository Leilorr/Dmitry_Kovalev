package org.example.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserEventDto {
    public enum EventType {
        CREATED, DELETED
    }

    private EventType eventType;
    private String email;
    private String name;
}