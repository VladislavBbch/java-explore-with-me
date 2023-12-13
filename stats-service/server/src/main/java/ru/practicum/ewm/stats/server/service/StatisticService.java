package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.StatisticRequestDto;
import ru.practicum.ewm.stats.dto.StatisticResponseDto;
import ru.practicum.ewm.stats.server.repository.StatisticRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;

    public void createHit(StatisticRequestDto requestDto) {
        statisticRepository.save(statisticMapper.toHit(requestDto));
    }

    public List<StatisticResponseDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала: " + start + " после даты окончания: " + end);
        }
        if (uris.size() > 0) {
            if (unique) {
                return statisticMapper.toStatisticDto(statisticRepository.statisticByUriInAndUniqueIp(start, end, uris));
            } else {
                return statisticMapper.toStatisticDto(statisticRepository.statisticByUriIn(start, end, uris));
            }
        } else {
            if (unique) {
                return statisticMapper.toStatisticDto(statisticRepository.statisticByAllUriAndUniqueIp(start, end));
            } else {
                return statisticMapper.toStatisticDto(statisticRepository.statisticByAllUri(start, end));
            }
        }
    }
}
