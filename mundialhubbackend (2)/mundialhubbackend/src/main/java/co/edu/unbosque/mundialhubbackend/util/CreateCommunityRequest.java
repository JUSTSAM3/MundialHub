package co.edu.unbosque.mundialhubbackend.util;

public class CreateCommunityRequest {

    private String name;
    private String description;

    public CreateCommunityRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}