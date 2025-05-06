package it.generation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EventDTO {
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate playDate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime playTime;
    
    private String description;
    private Long playingFieldId;


    public Long getId()             { return this.id;             }
    public LocalDate getPlayDate()  { return this.playDate;       }
    public LocalTime getPlayTime()  { return this.playTime;       }
    public String getDescription()  { return this.description;    }
    public Long getPlayingFieldId() { return this.playingFieldId; }




    public void setId(Long id)                          { this.id             = id;             }
    public void setPlayDate(LocalDate playDate)         { this.playDate       = playDate;       }
    public void setPlayTime(LocalTime playTime)         { this.playTime       = playTime;       }
    public void setDescription(String description)      { this.description    = description;    }
    public void setPlayingFieldId(Long playingFieldId)  { this.playingFieldId = playingFieldId; }


    @Override
    public String toString() {
        return String.format("playDate: %s, playTime: %s, description: %s, playingFieldId: %d", playDate, playTime, description, playingFieldId);
    }
}
