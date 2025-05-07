package it.generation.controller;

import it.generation.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/giocacongigi/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Assicura che solo gli admin possano accedere
public class AdminController {

    @Autowired
    private EventService eventService;

    @GetMapping("/statistics")
    public ResponseEntity<?> getEventStatistics() {
        // Ottieni il numero di eventi attivi (futuri)
        long activeEvents = eventService.getActiveEventsCount();

        // Ottieni il numero di eventi completati (passati)
        long completedEvents = eventService.getCompletedEventsCount();

        // Ottieni il numero totale di eventi creati
        long totalEvents = eventService.getTotalEventsCount();

        // Ottieni il numero totale delle iscrizioni attive
        long totalActiveSubscriptions = eventService.getTotalActiveSubscriptions();

        // Costruisci la risposta JSON
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("activeEvents", activeEvents);
        statistics.put("completedEvents", completedEvents);
        statistics.put("totalEvents", totalEvents);
        statistics.put("totalActiveSubscriptions", totalActiveSubscriptions);

        return ResponseEntity.ok(statistics);
    }
}