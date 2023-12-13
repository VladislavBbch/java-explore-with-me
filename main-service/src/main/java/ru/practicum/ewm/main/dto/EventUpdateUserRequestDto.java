package ru.practicum.ewm.main.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EventUpdateUserRequestDto extends EventRequestDto {
    private StateAction stateAction;

    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW,

    }
}
