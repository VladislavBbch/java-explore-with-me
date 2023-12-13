package ru.practicum.ewm.main.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EventUpdateAdminRequestDto extends EventRequestDto {
    private StateAction stateAction;

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
