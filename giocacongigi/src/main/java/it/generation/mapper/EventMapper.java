package it.generation.mapper;

import it.generation.dto.EventDTO;
import it.generation.model.Event;
import it.generation.model.PlayingField;
import it.generation.repository.PlayingFieldRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    private final PlayingFieldRepository playingFieldRepository;

    @Autowired
    public EventMapper(PlayingFieldRepository repository) {
        this.playingFieldRepository = repository;
    }

    public EventDTO toDTO(Event event) {
        if (event == null) { return null; }

        var dto = new EventDTO();
        dto.setId(event.getId());
        dto.setPlayDate(event.getPlayDate());
        dto.setPlayTime(event.getPlayTime());
        dto.setDescription(event.getDescription());
        if (event.getPlayingField() != null) {
            dto.setPlayingFieldId(event.getPlayingField().getId());
        }

        return dto;
    }

    public Optional<Event> toEntity(EventDTO dto) { 
        var event = new Event();
        event.setId(dto.getId());
        event.setPlayDate(dto.getPlayDate());
        event.setPlayTime(dto.getPlayTime());
        event.setDescription(dto.getDescription()); 

        if (dto.getPlayingFieldId() != null) {
            Optional<PlayingField> playingField = playingFieldRepository.findById(dto.getPlayingFieldId());
            if (playingField.isEmpty()) { 
                return Optional.empty();
            }
            event.setPlayingField(playingField.get());
        }

        return Optional.of(event); 
    }
}
