package it.generation.controller; 

import it.generation.dto.PlayingFieldDTO;
import it.generation.mapper.PlayingFieldMapper; 
import it.generation.model.PlayingField;
import it.generation.service.PlayingFieldService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; 
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fields")
public class PlayingFieldController {

    private final PlayingFieldService service;
    private final PlayingFieldMapper mapper;

    public PlayingFieldController(PlayingFieldService service, PlayingFieldMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<PlayingFieldDTO>> getAll() {
        return ResponseEntity.ok(service.findAll()
            .stream()
            .map(mapper::toDTO)  
            .collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<PlayingFieldDTO> create(@RequestBody PlayingFieldDTO dto) { 
        try {
            PlayingField savedField          = service.save(mapper.toEntity(dto));
            PlayingFieldDTO fieldDTOresponse = mapper.toDTO(savedField);

            return new ResponseEntity<>(fieldDTOresponse, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
             return new ResponseEntity<>(HttpStatus.CONFLICT);
        }  
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
