package it.generation.service;

import it.generation.model.Event;
import it.generation.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    @Autowired 
    public EventService(EventRepository repository) {
        this.eventRepository = repository;
    }

    public List<Event> findAll() {
        return this.eventRepository.findAll();
    }

    public Optional<Event> findById(Long id) {
        return this.eventRepository.findById(id);
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    public long getActiveEventsCount() {
        return eventRepository.countByPlayDateAfter(LocalDate.now());
    }

    // Conta gli eventi completati (passati)
    public long getCompletedEventsCount() {
        return eventRepository.countByPlayDateBefore(LocalDate.now());
    }

    // Conta il numero totale di eventi
    public long getTotalEventsCount() {
        return eventRepository.count();
    }

    // Conta il numero totale di iscrizioni attive
    public long getTotalActiveSubscriptions() {
        return eventRepository.findAll().stream()
                .mapToLong(event -> event.getUsers().size())
                .sum();
    }

}
