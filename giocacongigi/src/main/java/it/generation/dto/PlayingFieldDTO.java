package it.generation.dto;

public class PlayingFieldDTO {
    private Long id;
    private String name;
    private String description;

    public Long getId()             { return this.id;          }
    public String getName()         { return this.name;        }
    public String getDescription()  { return this.description; }

    public void setId(Long id)                      { this.id = id;                   }
    public void setName(String name)                { this.name = name;               }
    public void setDescription(String description)  { this.description = description; }
}
