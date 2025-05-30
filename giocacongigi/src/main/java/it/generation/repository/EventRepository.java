package it.generation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import it.generation.model.Event;
    
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByPlayingFieldId(Long playingFieldId);

    long countByPlayDateAfter(LocalDate currentDate);

    // Trova eventi con date passate rispetto a oggi
    long countByPlayDateBefore(LocalDate currentDate);

}