package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.model.EventState;
import ru.practicum.ewm.main.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.ewm.main.Constant.PATTERN;

@Data
@Builder(toBuilder = true)
public class EventResponseDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String title;
    private EventState state;
    private UserShortDto initiator;
    private Integer confirmedRequests;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN)
    private LocalDateTime publishedOn;
    private Integer views;
}