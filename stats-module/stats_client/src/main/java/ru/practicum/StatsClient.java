package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class StatsClient extends BaseClient {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<HitDto> addStatistic(HitDto hitDto) {
        return post("/hit", hitDto, HitDto.class);
    }

    public List<StatsDto> getStatistic(LocalDateTime startDate, LocalDateTime endDate, List<String> uris, Boolean unique) {
        Map<String, Object> params = Map.of(
                "start", startDate.format(formatter),
                "end", endDate.format(formatter),
                "uris", List.of(String.join(",", uris)),
                "unique", unique
        );

        ResponseEntity<StatsDto[]> response = get("stats?" + "start={start}&end={end}&uris={uris}&unique={unique}",
                params,
                StatsDto[].class);
        StatsDto[] stats = response.getBody();

        return Objects.nonNull(stats) ? List.of(stats) : new ArrayList<>();
    }

}
