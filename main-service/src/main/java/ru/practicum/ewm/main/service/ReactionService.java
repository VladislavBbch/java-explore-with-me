package ru.practicum.ewm.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.dto.ReactionDto;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ObjectNotFoundException;
import ru.practicum.ewm.main.exception.ValidateException;
import ru.practicum.ewm.main.model.*;
import ru.practicum.ewm.main.repository.EventRepository;
import ru.practicum.ewm.main.repository.ReactionRepository;
import ru.practicum.ewm.main.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final ReactionMapper reactionMapper;

    public ReactionDto addReaction(Long userId, Long eventId, Boolean isPositive) {
        eventRepository.checkEvent(eventId);
        Request request = requestRepository.findByUserIdAndEventId(userId, eventId);
        if (request == null || !request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidateException("Для добавления реакции необходима подтвержденная заявка на участие в событии");
        }
        ReactionId id = getReactionId(userId, eventId);
        Reaction existingReaction = reactionRepository.findById(id).orElse(null);
        if (existingReaction != null) {
            throw new ConflictException("Вы уже добавили реакцию на это событие ранее");
        }
        return reactionMapper.toReactionDto(
                reactionRepository.save(Reaction.builder()
                        .id(id)
                        .user(User.builder().id(userId).build())
                        .event(Event.builder().id(eventId).build())
                        .isPositive(isPositive)
                        .build()));
    }

    public ReactionDto updateReaction(Long userId, Long eventId, Boolean isPositive) {
        ReactionId id = getReactionId(userId, eventId);
        Reaction existingReaction = checkReaction(id);
        existingReaction.setIsPositive(isPositive);
        return reactionMapper.toReactionDto(
                reactionRepository.save(existingReaction));
    }

    public void deleteReaction(Long userId, Long eventId) {
        ReactionId id = getReactionId(userId, eventId);
        checkReaction(id);
        reactionRepository.deleteById(id);
    }

    private ReactionId getReactionId(Long userId, Long eventId) {
        return ReactionId.builder()
                .userId(userId)
                .eventId(eventId)
                .build();
    }

    private Reaction checkReaction(ReactionId id) {
        return reactionRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Реакция пользователя: " + id.getUserId() + " по событию: " + id.getEventId() + " не найдена"));
    }
}
