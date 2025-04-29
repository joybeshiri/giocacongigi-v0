package it.generation.repository;

import it.generation.model.PlayingField;  
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayingFieldRepository extends JpaRepository<PlayingField, Long> { 
}
