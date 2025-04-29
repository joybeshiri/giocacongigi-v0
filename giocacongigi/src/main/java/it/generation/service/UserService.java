package it.generation.service;

import it.generation.dto.RegisterRequest;
import it.generation.model.User;
import it.generation.repository.UserRepository;
import it.generation.security.JwtUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils        jwtUtils;

    @Autowired 
    public UserService(UserRepository repository, PasswordEncoder encoder, JwtUtils utils) {
        this.userRepository  = repository;
        this.passwordEncoder = encoder;
        this.jwtUtils        = utils;
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

            return RegisterResult.success(this.userRepository.save(user));
        } catch (Exception e) {
            return RegisterResult.FAILURE;
        }
    }

    public Optional<String> authenticate(String email, String rawPassword) {
        Optional<String> token = Optional.empty();
        Optional<User> user = this.getUserByEmail(email);

        if (user.isPresent()) {
            if (passwordEncoder.matches(rawPassword, user.get().getPassword())) {
                token = Optional.ofNullable(jwtUtils.generateToken(user.get().getEmail()));
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
}