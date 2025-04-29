package it.generation.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "play_date", nullable = false)
    private LocalDate playDate;

    @Column(name = "play_time", nullable = false)
    private LocalTime playTime;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "playing_field_id", nullable = false)
    private PlayingField playingField;

    @ManyToMany
    @JoinTable(
        name = "event_user",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    public Long getId()                     { return this.id; }
    public LocalDate getPlayDate()          { return this.playDate;     }
    public LocalTime getPlayTime()          { return this.playTime;     }
    public String getDescription()          { return this.description;  } 
    public PlayingField getPlayingField()   { return this.playingField; }
    public Set<User> getUsers()             { return this.users;        }

    public void setId(Long id)                                  { this.id             = id;           }
    public void setPlayDate(LocalDate playDate)                 { this.playDate       = playDate;     }
    public void setPlayTime(LocalTime playTime)                 { this.playTime       = playTime;     } 
    public void setDescription(String description)              { this.description    = description;  } 
    public void setPlayingField(PlayingField playingField)      { this.playingField   = playingField; }
    public void setUsers(Set<User> users)                       { this.users          = users;        }
}
