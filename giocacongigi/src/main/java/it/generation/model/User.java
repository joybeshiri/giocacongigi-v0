package it.generation.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;

@Entity
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @ManyToMany(mappedBy = "users")
    private Set<Event> events = new HashSet<>();

    public Long getId()             { return this.id;       }
    public String getEmail()        { return this.email;    }
    public String getName()         { return this.name;     }
    public String getPassword()     { return this.password; }
    public String getRole()         { return this.role;     }
    public Set<Event> getEvents()   { return this.events;   }

    public void setId(Long id)                  { this.id       = id;       }
    public void setEmail(String email)          { this.email    = email;    }
    public void setName(String name)            { this.name     = name;     }
    public void setPassword(String password)    { this.password = password; }
    public void setRole(String role)            { this.role     = role; }
    public void setEvents(Set<Event> events)    { this.events   = events;   }
}