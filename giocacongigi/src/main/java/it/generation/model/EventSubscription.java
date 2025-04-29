package it.generation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event_user")
@IdClass(EventSubscriptionId.class)
public class EventSubscription {

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;  

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;    

    public EventSubscription() {
    }

    public EventSubscription(Event event, User user) {
        this.event = event;
        this.user  = user;
    }

    public Event getEvent()     { return this.event; }
    public User getUser()       { return this.user;  }
    public Long getEventId()    { return event != null ? event.getId() : null; }
    public Long getUserId()     { return user  != null ? user.getId()  : null; }

    public void setEvent(Event event)   { this.event = event;   }
    public void setUser(User user)      { this.user  = user;    }
    public void setEventId(Long id)     { this.event.setId(id); }
    public void setUserId(Long id)      { this.user.setId(id);  }
}