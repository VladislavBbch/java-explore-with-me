package ru.practicum.ewm.main.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.model.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class EventRequestStatusUpdateRequest {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
