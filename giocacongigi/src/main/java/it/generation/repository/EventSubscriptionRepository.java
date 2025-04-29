package it.generation.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.generation.model.EventSubscription;
import it.generation.model.EventSubscriptionId;

@Repository
public interface EventSubscriptionRepository extends JpaRepository<EventSubscription, EventSubscriptionId> {
    List<EventSubscription> findByUser_Id(Long userId);
    Optional<EventSubscription> findByEvent_IdAndUser_Id(Long eventId, Long userId);
    boolean existsByEvent_IdAndUser_Id(Long eventId, Long userId); 
}
