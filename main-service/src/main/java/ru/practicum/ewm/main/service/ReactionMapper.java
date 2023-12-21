package ru.practicum.ewm.main.service;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.main.dto.ReactionDto;
import ru.practicum.ewm.main.model.Reaction;

@Component
public class ReactionMapper {
    public ReactionDto toReactionDto(Reaction reaction) {
        return ReactionDto.builder()
                .userId(reaction.getId().getUserId())
                .eventId(reaction.getId().getEventId())
                .isPositive(reaction.getIsPositive())
                .build();
    }
}
