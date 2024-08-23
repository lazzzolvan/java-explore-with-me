package ru.practicum.model.event;

import lombok.Getter;

@Getter
public enum EventState {
    PENDING,
    PUBLISHED,
    CANCELED;
}
