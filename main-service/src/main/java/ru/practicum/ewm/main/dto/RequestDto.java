package ru.practicum.ewm.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class RequestDto {
    private Long id;
    private Long requester;
    private Long event;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
    private RequestStatus status;
}
