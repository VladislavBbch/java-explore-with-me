package ru.practicum.ewm.main.service;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.dto.RequestDto;
import ru.practicum.ewm.main.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {
    public RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .requester(request.getUser().getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public List<RequestDto> toRequestDto(List<Request> requests) {
        return requests.stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());
    }
}
