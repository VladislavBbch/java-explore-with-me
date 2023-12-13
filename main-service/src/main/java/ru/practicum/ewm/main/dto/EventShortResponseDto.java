package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.ewm.main.Constant.PATTERN;

@Data
@Builder(toBuilder = true)
public class EventShortResponseDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private LocalDateTime eventDate;
    private Boolean paid;
    private String title;
    private UserShortDto initiator;
    private Integer confirmedRequests;
    private Integer views;
}