package it.generation.mapper;

import org.springframework.stereotype.Component;
import it.generation.dto.UserDTO;
import it.generation.model.User;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO(user.getId(), user.getEmail(),  user.getName(), user.getRole()); 
        return dto;
    }

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail()); 
        user.setName(dto.getName()); 
        user.setRole(dto.getRole()); 

        return user;
    }
}
