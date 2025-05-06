package it.generation.controller;

import it.generation.dto.AuthResponse;
import it.generation.dto.LoginRequest;
import it.generation.dto.RegisterRequest;
import it.generation.dto.UserDTO;
import it.generation.exception.ConflictException;
import it.generation.exception.InternalServerErrorException;
import it.generation.exception.UnauthorizedException;
import it.generation.mapper.UserMapper;
import it.generation.model.User; 
import it.generation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserMapper  userMapper;

    @Autowired
    public AuthController(UserService service, UserMapper mapper) {
        this.userService = service;
        this.userMapper  = mapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request)  {
        UserService.RegisterResult result = userService.register(request);
        switch (result) {
            case USER_ALREADY_EXISTS:
                throw new ConflictException("Utente già registrato");
            case FAILURE:
                throw new InternalServerErrorException("Errore sul server");
            case SUCCESS:
                User savedUser  = result.getSavedUser();
                return ResponseEntity.status(201).body(userMapper.toDTO(savedUser));
            default:
                throw new InternalServerErrorException("Errore sul server");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userService.authenticate(request.email, request.password)
            .map(token -> ResponseEntity.ok(new AuthResponse(token)))
            .orElseThrow(() -> new UnauthorizedException("Credenziali non valide"));
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmUser(@RequestBody Map<String, String> payload) {
        try {
            // Estrai il token dal corpo della richiesta
            String token = payload.get("token");
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token mancante.");
            }

            // Conferma l'utente
            userService.confirmUser(token);
            return ResponseEntity.ok("Registrazione confermata! Ora puoi accedere.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}