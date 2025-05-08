package it.generation.service;

import it.generation.model.Event;
import it.generation.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return eventRepository.findAll().stream()
                .filter(event -> {
                    // Combina la data e l'ora dell'evento
                    LocalDateTime eventDateTime = LocalDateTime.of(event.getPlayDate(), event.getPlayTime());
                    // Modifica la condizione per includere eventi nello stesso istante
                    return eventDateTime.isAfter(LocalDateTime.now()) || eventDateTime.isEqual(LocalDateTime.now());
                })
                .count(); // Conta il numero di eventi che soddisfano la condizione
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
                .filter(event -> {
                    LocalDateTime eventDateTime = LocalDateTime.of(event.getPlayDate(), event.getPlayTime());
                    return eventDateTime.isAfter(LocalDateTime.now()); // Evento attivo se nel futuro
                })
                .mapToLong(event -> event.getUsers().size()) // Conta gli utenti associati
                .sum();

    }

}
