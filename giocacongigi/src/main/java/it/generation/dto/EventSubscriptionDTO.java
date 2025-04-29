package it.generation.dto;

public class EventSubscriptionDTO {
    private Long eventId;
    private Long userId;
   
    public Long getEventId()    { return this.eventId; }
    public Long getUserId()     { return this.userId;  }
   
    public void setEventId(Long eventId)    { this.eventId = eventId; }
    public void setUserId(Long userId)      { this.userId  = userId;  }
}
