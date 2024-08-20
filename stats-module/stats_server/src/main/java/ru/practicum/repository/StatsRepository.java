package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {


    @Query("SELECT  new ru.practicum.model.Stats(app,  uri, count(distinct ip) AS hits) " +
            "FROM  EndpointHit " +
            "WHERE  timestamp BETWEEN  :start AND :end " +
            "GROUP BY app, uri ORDER BY hits DESC")
    List<Stats> getStatsWithUniqueIp(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    @Query("SELECT  new ru.practicum.model.Stats(app,  uri, count(distinct ip) AS hits) " +
            "FROM  EndpointHit " +
            "WHERE  timestamp BETWEEN  :start AND :end AND uri IN(:uris) " +
            "GROUP BY app, uri ORDER BY hits DESC")
    List<Stats> getStatsWithUniqueIpAndUrls(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.model.Stats(app, uri,count(ip) AS hits) " +
            "FROM  EndpointHit " +
            "WHERE timestamp BETWEEN :start AND :end " +
            "GROUP BY app, uri ORDER BY hits DESC")
    List<Stats> getStats(@Param("start") LocalDateTime start,
                         @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.model.Stats(app, uri,count(ip) AS hits) " +
            "FROM  EndpointHit " +
            "WHERE timestamp BETWEEN :start AND :end AND uri IN(:uris) " +
            "GROUP BY app, uri ORDER BY hits DESC")
    List<Stats> getStatsWithUrls(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("uris") List<String> uris);
}
