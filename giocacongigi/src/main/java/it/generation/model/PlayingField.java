package it.generation.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "playing_field")
public class PlayingField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "latitude", nullable = false)
    private double latitude;  // Aggiunto per la latitudine

    @Column(name = "longitude", nullable = false)
    private double longitude;  // Aggiunto per la longitudine

    @OneToMany(mappedBy = "playingField", cascade = CascadeType.ALL)
    private List<Event> events;

    public Long getId()             { return this.id;          }
    public String getName()         { return this.name;        }
    public String getDescription()  { return this.description; }
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}

    public List<Event> getEvents()  { return this.events;      }

    public void setId(Long id)                      { this.id          = id;          }
    public void setName(String name)                { this.name        = name;        }
    public void setDescription(String description)  { this.description = description; }
    public void setLatitude(double latitude) {this.latitude = latitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}
    public void setEvents(List<Event> events)       { this.events      = events;      }
}
