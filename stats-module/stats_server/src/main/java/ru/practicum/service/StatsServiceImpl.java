package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.InvalidTimeParamException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Stats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Override
    public HitDto create(HitDto hitDto) {
        repository.save(StatsMapper.toEndpointHit(hitDto));
        return hitDto;
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new InvalidTimeParamException("Время конца раньше начала");
        }

        if (uris == null) {
            return getAllWithoutUri(start, end).stream()
                    .map(StatsMapper::toDto)
                    .collect(Collectors.toList());
        }

        return unique.equals(true) ?
                getStatsWithUnique(start, end, uris).stream()
                        .map(StatsMapper::toDto)
                        .collect(Collectors.toList())
                : getAllStats(start, end, uris).stream()
                .map(StatsMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<Stats> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return uris.isEmpty() ? getAllWithoutUri(start, end) : repository.getStatsWithUrls(start, end, uris);
    }

    private List<Stats> getAllWithoutUri(LocalDateTime start, LocalDateTime end) {
        return repository.getStats(start, end);
    }

    private List<Stats> getStatsWithUnique(LocalDateTime start, LocalDateTime end, List<String> uris) {
        return uris.isEmpty() ? repository.getStatsWithUniqueIp(start, end) : repository.getStatsWithUniqueIpAndUrls(start, end, uris);

    }
}
