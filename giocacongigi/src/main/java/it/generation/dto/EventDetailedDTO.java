package it.generation.dto;

import java.util.Set;

public class EventDetailedDTO extends EventDTO {
    private PlayingFieldDTO playingField;
    private Set<UserDTO>    users;
    private boolean         joinable = false;
    private Double distance;


    public PlayingFieldDTO getPlayingField() { return this.playingField; }
    public Set<UserDTO> getUsers()           { return this.users;        }

    public boolean isJoinable()              { return this.joinable;     }

    public Double getDistance() {return distance;}

    public void setPlayingField(PlayingFieldDTO playingField) { this.playingField = playingField; }
    public void setUsers(Set<UserDTO> users)                  { this.users        = users;        }
    public void setJoinable(boolean joinable)                 { this.joinable     = joinable;     }
    public void setDistance(Double distance){ this.distance = distance; }
}
