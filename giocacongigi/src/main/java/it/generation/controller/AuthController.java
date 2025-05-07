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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AuthController(UserService service, UserMapper mapper) {
        this.userService = service;
        this.userMapper = mapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        UserService.RegisterResult result = userService.register(request);
        switch (result) {
            case USER_ALREADY_EXISTS:
                throw new ConflictException("Utente già registrato");
            case FAILURE:
                throw new InternalServerErrorException("Errore sul server");
            case SUCCESS:
                User savedUser = result.getSavedUser();
                return ResponseEntity.status(201).body(userMapper.toDTO(savedUser));
            default:
                throw new InternalServerErrorException("Errore sul server");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userService.authenticate(request.email, request.password).map(token -> ResponseEntity.ok(new AuthResponse(token))).orElseThrow(() -> new UnauthorizedException("Credenziali non valide"));
    }



    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token)
                                         {
        try {
            boolean isConfirmed = userService.confirmUser(token);

            if (isConfirmed) {
                // Usa il parametro redirectTo se è fornito
                String redirectUrl ="http://localhost:8080/giocacongigi/";
                return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
            } else {
                return ResponseEntity.status(HttpStatus.FOUND).header("Location", "http://localhost:8080/giocacongigi/error?message=invalid_token").build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la conferma del token.");
        }
    }
}