package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class functionEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private String name;
    private String type;
    private Long author_id;


    public UUID getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public String getType(){
        return this.type;
    }

    public Long getAuthor_id() {
        return this.author_id;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public void setPassword_hash(String type) {
        this.type = type;
    }

    public void setPassword_hash(Long author_id) {
        this.author_id = author_id;
    }
}
