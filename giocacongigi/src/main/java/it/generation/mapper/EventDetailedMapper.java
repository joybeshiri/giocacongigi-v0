package it.generation.mapper;

import java.util.stream.Collectors;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import it.generation.dto.EventDTO;
import it.generation.dto.EventDetailedDTO;
import it.generation.dto.PlayingFieldDTO;
import it.generation.dto.UserDTO;
import it.generation.model.Event;
import it.generation.repository.PlayingFieldRepository;

@Component
public class EventDetailedMapper extends EventMapper { 
    private final PlayingFieldMapper playingFieldMapper;
    private final UserMapper         userMapper; 

    @Autowired
    public EventDetailedMapper(PlayingFieldMapper playingFieldMapper, UserMapper userMapper, PlayingFieldRepository repository) {
        super(repository);

        this.playingFieldMapper = playingFieldMapper;
        this.userMapper         = userMapper;
    }

    public EventDetailedDTO toDetailedDTO(Event event) {
        if (event == null) { return null; }

        EventDetailedDTO eventDetailedDTO = new EventDetailedDTO();
        
        // Mappa i campi di EventDTO (riutilizzando EventMapper)
        EventDTO eventDTO = super.toDTO(event);
        eventDetailedDTO.setId(eventDTO.getId());
        eventDetailedDTO.setPlayDate(eventDTO.getPlayDate());
        eventDetailedDTO.setPlayTime(eventDTO.getPlayTime());
        eventDetailedDTO.setDescription(eventDTO.getDescription());
        eventDetailedDTO.setPlayingFieldId(eventDTO.getPlayingFieldId());


        // Mappa il campo da gioco
        PlayingFieldDTO playingFieldDTO = playingFieldMapper.toDTO(event.getPlayingField());
        eventDetailedDTO.setPlayingField(playingFieldDTO);

        // Mappa gli utenti
        Set<UserDTO> userDTOs = event.getUsers().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toSet());
        eventDetailedDTO.setUsers(userDTOs);

        return eventDetailedDTO;
    }
}
