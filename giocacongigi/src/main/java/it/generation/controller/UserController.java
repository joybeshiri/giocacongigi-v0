package it.generation.controller;

import it.generation.dto.UserDTO;
import it.generation.exception.ResourceNotFoundException;
import it.generation.mapper.UserMapper;
import it.generation.model.User;
import it.generation.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserMapper  userMapper;

    @Autowired
    public UserController(UserService service, UserMapper mapper) {
        this.userService = service;
        this.userMapper  = mapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal String email) {
        Optional<User> userOptional = userService.getUserByEmail(email);

        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Utente non trovato");
        }
        
        return new ResponseEntity<>(userMapper.toDTO(userOptional.get()), HttpStatus.OK);
    }
}