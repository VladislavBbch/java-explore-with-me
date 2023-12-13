package ru.practicum.ewm.main.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class EventRequestStatusUpdateResponse {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
