package it.generation.service;

import it.generation.dto.RegisterRequest;
import it.generation.exception.ResourceNotFoundException;
import it.generation.model.User;
import it.generation.repository.UserRepository;
import it.generation.security.JwtUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import it.generation.service.EmailService;

@Service
public class UserService {
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils        jwtUtils;
    private final EmailService    emailService; // Aggiungi questo campo

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder, JwtUtils utils, EmailService emailService
    ) {
        this.userRepository  = repository;
        this.passwordEncoder = encoder;
        this.jwtUtils        = utils;
        this.emailService = emailService;
    }

    public RegisterResult register(RegisterRequest request) {
        if (this.userRepository.findByEmail(request.email).isPresent()) {
            return RegisterResult.USER_ALREADY_EXISTS;
        }

        try {
            User user = new User();
            user.setEmail(request.email);
            user.setName(request.name);
            user.setRole(request.role);
            user.setPassword(passwordEncoder.encode(request.password));

            // Genera un token univoco di conferma
            String confirmationToken = UUID.randomUUID().toString();
            user.setConfirmationToken(confirmationToken);

            // Salva l'utente nel database
            User savedUser = this.userRepository.save(user);

            // Invia un'email con il link di conferma
            emailService.sendConfirmationEmail(user.getEmail(), user.getName(), confirmationToken);
            System.out.println("Email di conferma inviata a: " + user.getEmail());

            return RegisterResult.success(savedUser);
        } catch (Exception e) {
            return RegisterResult.FAILURE;
        }
    }


    public Optional<String> authenticate(String email, String rawPassword) {
        Optional<String> token = Optional.empty();
        Optional<User> user = this.getUserByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(rawPassword, user.get().getPassword())) {
                // Ottieni il ruolo dell'utente
                String role = user.get().getRole();
                // Passa email e ruolo al metodo generateToken
                token = Optional.ofNullable(jwtUtils.generateToken(user.get().getEmail(), role));
            }
        }
        return token;
    }


    public Optional<User> getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {
        return this.userRepository.findById(userId);
    }

    public enum RegisterResult {
        USER_ALREADY_EXISTS,
        FAILURE,
        SUCCESS;

        private User savedUser;

        public static RegisterResult success(User savedUser) {
            RegisterResult result = SUCCESS;
            result.savedUser = savedUser;
            return result;
        }

        public User getSavedUser() {
            return savedUser;
        }
    }
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("La password attuale non è corretta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public boolean confirmUser(String token) {
        // Controllo di validità del token (pre-condizione)
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token mancante.");
        }

        // Trova l'utente basandoti sul token
        Optional<User> optionalUser = userRepository.findByConfirmationToken(token);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Token non valido o inesistente.");
        }

        User user = optionalUser.get();

        // Prevenzione: se l'utente è già confermato, solleva un'eccezione
        if (user.getConfirmationToken() == null) {
            throw new IllegalArgumentException("Token già usato o utente già confermato.");
        }

        // Aggiorna lo stato dell'utente
        user.setConfirmationToken(null); // Rimuovi il token
        user.setRole("USER"); // Imposta il ruolo o lo stato desiderato

        // Effettua il salvataggio dopo tutte le validazioni
        userRepository.save(user);

        return true; // Conferma avvenuta
    }



}
