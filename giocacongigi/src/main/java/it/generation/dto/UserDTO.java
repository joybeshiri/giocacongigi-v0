package it.generation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Email obbligatorio")
    @Email(message = "Email non valida")
    private String email;

    @NotBlank(message = "Nome obbligatorio")
    private String name;

    @NotBlank(message = "Tipo obbligatorio")
    private String role;

    public UserDTO(Long id, String email, String name, String role) {
        this.id    = id;
        this.email = email;
        this.name  = name;
        this.role  = role;
    }

    public Long getId()         { return this.id;    }
    public String getEmail()    { return this.email; }
    public String getName()     { return this.name;  }
    public String getRole()     { return this.role;  }
    
    public void setId(Long id)          { this.id = id;       }
    public void setEmail(String email)  { this.email = email; }
    public void setName(String name)    { this.name = name;   }
    public void setRole(String role)    { this.role = role;   }
}
