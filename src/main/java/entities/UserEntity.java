package entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    private String username;
    private String password_hash;

    public UserEntity(UUID id, String username, String password_hash) {
        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
    }

    public UUID getId(){
        return this.id;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPassword_hash(){
        return this.password_hash;
    }


    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }
}
