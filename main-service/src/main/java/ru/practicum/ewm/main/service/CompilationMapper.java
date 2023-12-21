package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.dto.CompilationRequestDto;
import ru.practicum.ewm.main.dto.CompilationResponseDto;
import ru.practicum.ewm.main.model.Compilation;
import ru.practicum.ewm.main.model.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public Compilation toCompilation(CompilationRequestDto compilationDto, Set<Event> events) {
        return Compilation.builder()
                .isPinned(compilationDto.getIsPinned())
                .title(compilationDto.getTitle())
                .events(events)
                .build();
    }

    public CompilationResponseDto toCompilationDto(Compilation compilation,
                                                   Map<Long, Integer> requestsCountByEventId,
                                                   Map<Long, Integer> viewsByEventId,
                                                   Map<Long, Double> ratingsByEventId) {
        return CompilationResponseDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getIsPinned())
                .title(compilation.getTitle())
                .events(eventMapper.toEventShortDto(
                        List.copyOf(compilation.getEvents()), requestsCountByEventId, viewsByEventId, ratingsByEventId))
                .build();
    }

    public List<CompilationResponseDto> toCompilationDto(List<Compilation> compilations,
                                                         Map<Long, Integer> requestsCountByEventId,
                                                         Map<Long, Integer> viewsByEventId,
                                                         Map<Long, Double> ratingsByEventId) {
        return compilations.stream()
                .map(compilation -> toCompilationDto(compilation, requestsCountByEventId, viewsByEventId, ratingsByEventId))
                .collect(Collectors.toList());
    }
}
