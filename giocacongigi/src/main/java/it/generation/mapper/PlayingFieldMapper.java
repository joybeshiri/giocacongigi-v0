package it.generation.mapper;

import it.generation.dto.PlayingFieldDTO;
import it.generation.model.PlayingField;
import org.springframework.stereotype.Component;

@Component
public class PlayingFieldMapper {

    public PlayingFieldDTO toDTO(PlayingField entity) {
        var dto = new PlayingFieldDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

        return dto;
    }

    public PlayingField toEntity(PlayingFieldDTO dto) {
        var entity = new PlayingField();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        
        return entity;
    }
}
