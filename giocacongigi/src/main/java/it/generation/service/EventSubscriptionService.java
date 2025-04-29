package it.generation.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.generation.model.Event;
import it.generation.model.EventSubscription;
import it.generation.model.User;
import it.generation.repository.EventSubscriptionRepository;

@Service
public class EventSubscriptionService {
    private final EventSubscriptionRepository eventSubscriptionRepository;

    @Autowired
    public EventSubscriptionService(EventSubscriptionRepository repository) {
        this.eventSubscriptionRepository = repository;
    }

    public List<EventSubscription> findByUserId(Long userId) {
        return this.eventSubscriptionRepository.findByUser_Id(userId);
    }

    public Optional<EventSubscription> findByUserIdAndEventId(Long userId, Long eventId) {
        return this.eventSubscriptionRepository.findByEvent_IdAndUser_Id(userId, eventId);
    }

    public boolean existsByEventIdAndUserId(Long eventId, Long userId) {
        return this.eventSubscriptionRepository.existsByEvent_IdAndUser_Id(eventId, userId);
    }

    public EventSubscription save(EventSubscription eventSubscription) {
        return this.eventSubscriptionRepository.save(eventSubscription);
    }

    public EventSubscription subscribeUserToEvent(Long userId, Long eventId) {
        EventSubscription eventSubscription = new EventSubscription();
        // Assuming you have a way to get the Event and User entities by their IDs
        // (e.g., through EventService and UserService)
        Event event = new Event();
        event.setId(eventId);
        User user = new User();
        user.setId(userId);
        eventSubscription.setEvent(event);
        eventSubscription.setUser(user);

        return this.eventSubscriptionRepository.save(eventSubscription);
    }

    public void delete(EventSubscription eventUnSubscription) {
        this.eventSubscriptionRepository.delete(eventUnSubscription);
    }
}
