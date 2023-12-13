package ru.practicum.ewm.main.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.CompilationRequestDto;
import ru.practicum.ewm.main.dto.CompilationResponseDto;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.model.Compilation;
import ru.practicum.ewm.main.model.Event;
import ru.practicum.ewm.main.model.QCompilation;
import ru.practicum.ewm.main.repository.CompilationRepository;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.RequestRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final ViewsUtils viewsUtils;

    public CompilationResponseDto createCompilation(CompilationRequestDto compilationDto) {
        List<Long> eventIds = List.of();
        if (compilationDto.getEvents() != null) {
            eventIds = new ArrayList<>(compilationDto.getEvents());
        }
        List<Event> events = List.of();
        if (eventIds.size() > 0) {
            events = eventRepository.findAllById(eventIds);
        }
        Compilation compilation = compilationMapper.toCompilation(compilationDto, Set.copyOf(events));
        if (compilation.getIsPinned() == null) {
            compilation.setIsPinned(false);
        }
        return compilationMapper.toCompilationDto(
                compilationRepository.save(compilation),
                requestRepository.getConfirmedRequestsCountByEvents(eventIds),
                viewsUtils.getViewsByEvents(eventIds, events));
    }

    public List<CompilationResponseDto> getCompilations(Boolean isPinned, Integer from, Integer size) {
        BooleanExpression query = QCompilation.compilation.id.isNotNull();
        if (isPinned != null) {
            query = query.and(QCompilation.compilation.isPinned.eq(isPinned));
        }
        List<Compilation> compilations = compilationRepository.findAll(query, PageRequestByElement.of(from, size)).toList();
        List<Event> compilationsEvents = new ArrayList<>();
        List<Long> compilationsEventIds = new ArrayList<>();
        for (Compilation compilation : compilations) {
            List<Event> compilationEvents = List.copyOf(compilation.getEvents());
            compilationsEvents.addAll(compilationEvents);
            compilationsEventIds.addAll(compilationEvents.stream().map(Event::getId).collect(toList()));
        }
        return compilationMapper.toCompilationDto(
                compilations,
                requestRepository.getConfirmedRequestsCountByEvents(compilationsEventIds),
                viewsUtils.getViewsByEvents(compilationsEventIds, compilationsEvents));
    }

    public CompilationResponseDto getCompilationById(Long id) {
        Compilation compilation = checkCompilation(id);
        List<Long> eventIds = compilation.getEvents().stream().map(Event::getId).collect(toList());
        return compilationMapper.toCompilationDto(
                compilation,
                requestRepository.getConfirmedRequestsCountByEvents(eventIds),
                viewsUtils.getViewsByEvents(eventIds, compilation.getEvents()));
    }

    public CompilationResponseDto updateCompilation(Long id, CompilationRequestDto compilationDto) {
        Compilation existingCompilation = checkCompilation(id);
        List<Long> eventIds = List.of();
        if (compilationDto.getEvents() != null) {
            eventIds = new ArrayList<>(compilationDto.getEvents());
        }
        List<Event> events = List.of();
        if (eventIds.size() > 0) {
            events = eventRepository.findAllById(eventIds);
            existingCompilation.setEvents(new HashSet<>(events));
        }
        Boolean isPinned = compilationDto.getIsPinned();
        if (isPinned != null) {
            existingCompilation.setIsPinned(isPinned);
        }
        String title = compilationDto.getTitle();
        if (title != null && !title.isBlank()) {
            existingCompilation.setTitle(title);
        }
        return compilationMapper.toCompilationDto(
                compilationRepository.save(existingCompilation),
                requestRepository.getConfirmedRequestsCountByEvents(eventIds),
                viewsUtils.getViewsByEvents(eventIds, events));
    }

    public void deleteCompilation(Long id) {
        checkCompilation(id);
        compilationRepository.deleteById(id);
    }

    private Compilation checkCompilation(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Несуществующий id подборки событий: " + id));
    }
}
