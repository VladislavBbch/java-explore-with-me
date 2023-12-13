package ru.practicum.ewm.stats.server.service;

import org.springframework.stereotype.Component;

import ru.practicum.ewm.stats.dto.StatisticRequestDto;
import ru.practicum.ewm.stats.dto.StatisticResponseDto;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.model.StatisticInfo;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatisticMapper {
    public Hit toHit(StatisticRequestDto requestDto) {
        return Hit.builder()
                .application(requestDto.getApp())
                .uri(requestDto.getUri())
                .ip(requestDto.getIp())
                .timestamp(requestDto.getTimestamp())
                .build();
    }

    public List<StatisticResponseDto> toStatisticDto(List<StatisticInfo> hits) {
        return hits.stream()
                .map(hit -> StatisticResponseDto.builder()
                        .app(hit.getApp())
                        .uri(hit.getUri())
                        .hits(hit.getHits())
                        .build())
                .collect(Collectors.toList());
    }
}
