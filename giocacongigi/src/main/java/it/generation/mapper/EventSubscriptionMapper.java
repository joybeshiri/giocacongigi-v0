package it.generation.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import it.generation.dto.EventSubscriptionDTO;
import it.generation.model.Event;
import it.generation.model.EventSubscription;
import it.generation.model.User;
import it.generation.service.EventService;
import it.generation.service.UserService;

@Component
public class EventSubscriptionMapper {  
    private final EventService eventService;
    private final UserService  userService;

    @Autowired
    public EventSubscriptionMapper(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService  = userService;
    }

    public EventSubscriptionDTO toDTO(EventSubscription eventSubscription) {
        if (eventSubscription == null) {
            return null;
        }

        EventSubscriptionDTO dto = new EventSubscriptionDTO();
        dto.setUserId(eventSubscription.getUserId());
        dto.setEventId(eventSubscription.getEventId());

        return dto;
    }

    public EventSubscription toEntity(EventSubscriptionDTO eventSubscriptionDTO) {
        if (eventSubscriptionDTO == null) {
            return null;

        }
        EventSubscription entity = new EventSubscription();
        if (eventSubscriptionDTO.getEventId() != null) {
            Event event = eventService.findById(eventSubscriptionDTO.getEventId()).orElse(null);
            entity.setEvent(event);
        }
        if (eventSubscriptionDTO.getUserId() != null) {
            User user = userService.findById(eventSubscriptionDTO.getUserId()).orElse(null);
            entity.setUser(user);
        }

        return entity;
    }
}
