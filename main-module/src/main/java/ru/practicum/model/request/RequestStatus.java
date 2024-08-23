package ru.practicum.model.request;

import lombok.Getter;

@Getter
public enum RequestStatus {

    CONFIRMED,

    PENDING,

    REJECTED,

    CANCELED
}