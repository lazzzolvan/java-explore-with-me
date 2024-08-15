package ru.practicum.stats;

import ru.practicum.dto.EndpointHitDto;

import java.sql.Timestamp;

public class EndpointHitMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        if (endpointHit == null) {
            return null;
        }
        return EndpointHitDto.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp().toString())
                .build();
    }

    public static EndpointHit toEndpointHit(int id, EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .id(id)
                .app(endpointHitDto.getApp())
                .ip(endpointHitDto.getIp())
                .uri(endpointHitDto.getUri())
                .timestamp(Timestamp.valueOf(endpointHitDto.getTimestamp()))
                .build();
    }
}