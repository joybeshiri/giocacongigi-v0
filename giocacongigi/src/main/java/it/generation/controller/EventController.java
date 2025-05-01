/*package it.generation.controller;

import it.generation.dto.EventDTO;
import it.generation.dto.EventDetailedDTO;
import it.generation.dto.EventSubscriptionDTO;
import it.generation.exception.BadRequestException;
import it.generation.exception.ResourceNotFoundException;
import it.generation.mapper.EventDetailedMapper;
import it.generation.mapper.EventSubscriptionMapper;
import it.generation.model.Event;
import it.generation.model.EventSubscription;
import it.generation.model.User;
import it.generation.service.EventService;
import it.generation.service.EventSubscriptionService;
import it.generation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService              eventService;
    private final EventDetailedMapper       eventDetailedMapper;
    private final UserService               userService;
    private final EventSubscriptionService  eventSubscriptionService;
    private final EventSubscriptionMapper   eventSubscriptionMapper;

    @Autowired
    public EventController(
        EventService                service,
        EventDetailedMapper         detailedMapper,
        UserService                 userService,
        EventSubscriptionService    eventSubscriptionService,
        EventSubscriptionMapper     eventSubscriptionMapper
    ) {
        this.eventService             = service;
        this.eventDetailedMapper      = detailedMapper;
        this.userService              = userService;
        this.eventSubscriptionService = eventSubscriptionService;
        this.eventSubscriptionMapper  = eventSubscriptionMapper;
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAll() {
        return ResponseEntity.ok(eventService.findAll()
            .stream()
            .map(eventDetailedMapper::toDTO)
            .collect(Collectors.toList())
        );
    }

    @GetMapping("/detailed")
    public ResponseEntity<List<EventDetailedDTO>> getDetailedEvents() {
        return ResponseEntity.ok(eventService.findAll()
            .stream()
            .map(eventDetailedMapper::toDetailedDTO)
            .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}/detailed")
    public ResponseEntity<EventDetailedDTO> getEventDetailed(@PathVariable Long id) {
        Optional<Event> optionalEvent = eventService.findById(id);

        if (optionalEvent.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        return new ResponseEntity<>(eventDetailedMapper.toDetailedDTO(optionalEvent.get()), HttpStatus.OK);
    }

    @GetMapping("/joinable/{user_id}")
    public ResponseEntity<List<EventDetailedDTO>> getEventJoanableByUser(@PathVariable Long user_id) {
        return ResponseEntity.ok(eventService.findAll()
            .stream()
            .filter(event -> event.getPlayDate().isAfter(LocalDate.now()))
            .map(eventDetailedMapper::toDetailedDTO)
            .map(dto -> {
                dto.setJoinable(!dto.getUsers()
                    .stream()
                    .anyMatch(user -> user.getId().equals(user_id)));
                return dto;
            })
            .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<EventDTO> create(@RequestBody EventDTO eventDTO) {
        Optional<Event> eventOptional = eventDetailedMapper.toEntity(eventDTO);

        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        Event event      = eventOptional.get();
        Event savedEvent = eventService.save(event);

        EventDTO eventoDTOresponse = eventDetailedMapper.toDTO(savedEvent);

        return new ResponseEntity<>(eventoDTOresponse, HttpStatus.CREATED);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeUserToEvent(@RequestBody EventSubscriptionDTO eventSubscriptionDTO) {
        Optional<Event> eventOptional = eventService.findById(eventSubscriptionDTO.getEventId());
        Optional<User>  userOptional  = userService.findById(eventSubscriptionDTO.getUserId());

        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Utente non trovato");
        }

        Event event = eventOptional.get();
        User user   = userOptional.get();

        if (event.getPlayDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Non è possibile iscriversi a eventi passati");
        }

        if (eventSubscriptionService.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new BadRequestException("L'utente già iscritto a questo evento");
        }

        EventSubscription eventSubscription = eventSubscriptionMapper.toEntity(eventSubscriptionDTO);
        eventSubscription.setEvent(event);
        eventSubscription.setUser(user);
        eventSubscriptionService.save(eventSubscription);

        event.getUsers().add(user);
        eventService.save(event);

        return new ResponseEntity<String>("Iscrizione avvenuta con successo", HttpStatus.OK);
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribeUserFromEvent(@RequestBody EventSubscriptionDTO eventUnSubscriptionDTO) {
        Optional<Event> eventOptional = eventService.findById(eventUnSubscriptionDTO.getEventId());
        Optional<User>  userOptional  = userService.findById(eventUnSubscriptionDTO.getUserId());

        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Utente non trovato");
        }

        Event event = eventOptional.get();
        User user = userOptional.get();

        if (!eventSubscriptionService.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new BadRequestException("L'utente non risulta iscritto a questo evento");
        }

        EventSubscription eventUnSubscription = eventSubscriptionMapper.toEntity(eventUnSubscriptionDTO);
        eventUnSubscription.setEvent(event);
        eventUnSubscription.setUser(user);
        eventSubscriptionService.delete(eventUnSubscription);

        return new ResponseEntity<String>("Disiscrizione avvenuta con successo", HttpStatus.OK);
    }
}*/
package it.generation.controller;

import it.generation.dto.EventDTO;
import it.generation.dto.EventDetailedDTO;
import it.generation.dto.EventSubscriptionDTO;
import it.generation.exception.BadRequestException;
import it.generation.exception.ResourceNotFoundException;
import it.generation.mapper.EventDetailedMapper;
import it.generation.mapper.EventSubscriptionMapper;
import it.generation.model.Event;
import it.generation.model.EventSubscription;
import it.generation.model.User;
import it.generation.service.EventService;
import it.generation.service.EventSubscriptionService;
import it.generation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventDetailedMapper eventDetailedMapper;
    private final UserService userService;
    private final EventSubscriptionService eventSubscriptionService;
    private final EventSubscriptionMapper eventSubscriptionMapper;

    @Autowired
    public EventController(
            EventService service,
            EventDetailedMapper detailedMapper,
            UserService userService,
            EventSubscriptionService eventSubscriptionService,
            EventSubscriptionMapper eventSubscriptionMapper
    ) {
        this.eventService = service;
        this.eventDetailedMapper = detailedMapper;
        this.userService = userService;
        this.eventSubscriptionService = eventSubscriptionService;
        this.eventSubscriptionMapper = eventSubscriptionMapper;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<EventDTO>> getAll() {
        return ResponseEntity.ok(eventService.findAll()
                .stream()
                .map(eventDetailedMapper::toDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/detailed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EventDetailedDTO>> getDetailedEvents() {
        return ResponseEntity.ok(eventService.findAll()
                .stream()
                .map(eventDetailedMapper::toDetailedDTO)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}/detailed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventDetailedDTO> getEventDetailed(@PathVariable Long id) {
        Optional<Event> optionalEvent = eventService.findById(id);

        if (optionalEvent.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        return new ResponseEntity<>(eventDetailedMapper.toDetailedDTO(optionalEvent.get()), HttpStatus.OK);
    }

    @GetMapping("/joinable/{user_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EventDetailedDTO>> getEventJoanableByUser(@PathVariable Long user_id) {
        return ResponseEntity.ok(eventService.findAll()
                .stream()
                .filter(event -> event.getPlayDate().isAfter(LocalDate.now()))
                .map(eventDetailedMapper::toDetailedDTO)
                .map(dto -> {
                    dto.setJoinable(!dto.getUsers()
                            .stream()
                            .anyMatch(user -> user.getId().equals(user_id)));
                    return dto;
                })
                .collect(Collectors.toList())
        );
    }

    //CREA EVENTO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")  // Solo admin può creare eventi
    public ResponseEntity<EventDTO> create(@RequestBody EventDTO eventDTO) {
        Optional<Event> eventOptional = eventDetailedMapper.toEntity(eventDTO);

        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        Event event = eventOptional.get();
        Event savedEvent = eventService.save(event);
        EventDTO eventoDTOresponse = eventDetailedMapper.toDTO(savedEvent);

        return new ResponseEntity<>(eventoDTOresponse, HttpStatus.CREATED);
    }

    //CANCELLA EVENTO
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Optional<Event> eventOptional = eventService.findById(id);

        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento con ID " + id + " non trovato");
        }

        eventService.delete(id);  // usa il  metodo esistente in event service delete
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }


    // MODIFICA EVENTO
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        // Trova l'evento esistente
        Optional<Event> existingEventOptional = eventService.findById(id);

        if (existingEventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento con ID " + id + " non trovato");
        }
       // Event existingEvent = existingEventOptional.get();

        eventDTO.setId(id);

        Optional<Event> event = this.eventDetailedMapper.toEntity(eventDTO);

        // Salva l'evento aggiornato
        Event updatedEvent = eventService.save(event.get());

        // Restituisci la risposta con l'evento aggiornato
        EventDTO updatedEventDTO = eventDetailedMapper.toDTO(updatedEvent);
        return new ResponseEntity<>(updatedEventDTO, HttpStatus.OK);
    }




    @PostMapping("/subscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> subscribeUserToEvent(@RequestBody EventSubscriptionDTO eventSubscriptionDTO) {
        Optional<Event> eventOptional = eventService.findById(eventSubscriptionDTO.getEventId());
        Optional<User> userOptional = userService.findById(eventSubscriptionDTO.getUserId());

        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Utente non trovato");
        }

        Event event = eventOptional.get();
        User user = userOptional.get();

        if (event.getPlayDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Non è possibile iscriversi a eventi passati");
        }

        if (eventSubscriptionService.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new BadRequestException("L'utente già iscritto a questo evento");
        }

        EventSubscription eventSubscription = eventSubscriptionMapper.toEntity(eventSubscriptionDTO);
        eventSubscription.setEvent(event);
        eventSubscription.setUser(user);
        eventSubscriptionService.save(eventSubscription);

        event.getUsers().add(user);
        eventService.save(event);

        return new ResponseEntity<>("Iscrizione avvenuta con successo", HttpStatus.OK);
    }

    @DeleteMapping("/unsubscribe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unsubscribeUserFromEvent(@RequestBody EventSubscriptionDTO eventUnSubscriptionDTO) {
        Optional<Event> eventOptional = eventService.findById(eventUnSubscriptionDTO.getEventId());
        Optional<User> userOptional = userService.findById(eventUnSubscriptionDTO.getUserId());

        if (eventOptional.isEmpty()) {
            throw new ResourceNotFoundException("Evento non trovato");
        }

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Utente non trovato");
        }

        Event event = eventOptional.get();
        User user = userOptional.get();

        if (!eventSubscriptionService.existsByEventIdAndUserId(event.getId(), user.getId())) {
            throw new BadRequestException("L'utente non risulta iscritto a questo evento");
        }

        EventSubscription eventUnSubscription = eventSubscriptionMapper.toEntity(eventUnSubscriptionDTO);
        eventUnSubscription.setEvent(event);
        eventUnSubscription.setUser(user);
        eventSubscriptionService.delete(eventUnSubscription);

        return new ResponseEntity<>("Disiscrizione avvenuta con successo", HttpStatus.OK);
    }
}

