package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class userEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private String username;
    private Long password_hash;


    public UUID getId(){
        return this.id;
    }

    public String getUsername(){
        return this.username;
    }

    public Long getPassword_hash(){
        return this.password_hash;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword_hash(Long password_hash) {
        this.password_hash = password_hash;
    }
}
