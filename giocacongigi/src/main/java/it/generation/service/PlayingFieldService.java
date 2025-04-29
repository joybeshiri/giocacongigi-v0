package it.generation.service;

import it.generation.model.PlayingField;
import it.generation.repository.PlayingFieldRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PlayingFieldService {
    private final PlayingFieldRepository playingFieldRepository; 

    @Autowired 
    public PlayingFieldService(PlayingFieldRepository repository) {
        this.playingFieldRepository = repository;
    }

    public List<PlayingField> findAll() {
        return playingFieldRepository.findAll();
    }

    public Optional<PlayingField> findById(Long id) {
        return playingFieldRepository.findById(id);
    }

    public PlayingField save(PlayingField field) {
        return playingFieldRepository.save(field);
    }

    public void delete(Long id) {
        playingFieldRepository.deleteById(id);
    }
}
