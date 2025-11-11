package com.oop.labs.entities;

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
    private UUID author_id;


    public UUID getId(){
        return this.id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getType(){
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }


    public UUID getAuthor_id() {
        return this.author_id;
    }

    public void setAuthor_id(UUID author_id) {
        this.author_id = author_id;
    }

}
