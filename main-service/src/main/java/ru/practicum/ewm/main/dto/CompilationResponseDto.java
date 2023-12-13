package ru.practicum.ewm.main.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CompilationResponseDto {
    private Long id;
    private List<EventShortResponseDto> events;
    private Boolean pinned;
    private String title;
}
