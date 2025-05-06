package it.generation.dto;

public class PlayingFieldDTO {
    private Long id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;

    public Long getId()             { return this.id;          }
    public String getName()         { return this.name;        }
    public String getDescription()  { return this.description; }
    public Double getLatitude() {return latitude;}
    public Double getLongitude() {return longitude;}


    public void setId(Long id)                      { this.id = id;                   }
    public void setName(String name)                { this.name = name;               }
    public void setDescription(String description)  { this.description = description; }
    public void setLatitude(Double latitude) {this.latitude = latitude;}
    public void setLongitude(Double longitude) {this.longitude = longitude;}
}
