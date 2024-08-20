package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Stats;

@UtilityClass
public class StatsMapper {
    public static EndpointHit toEndpointHit(HitDto hitDto) {
        return EndpointHit.builder()
                .id(hitDto.getId())
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }

    public static StatsDto toDto(Stats stats) {
        return StatsDto.builder()
                .hits(stats.getHits())
                .app(stats.getApp())
                .uri(stats.getUri())
                .build();
    }
}
