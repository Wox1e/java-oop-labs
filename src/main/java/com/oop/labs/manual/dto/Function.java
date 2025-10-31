package com.oop.labs.manual.dto;

public class Function {
    private int id;
    private String name;
    private String type;
    private int authorId;

    public Function(String name, String type, int authorId) {
        this.name = name;
        this.type = type;
        this.authorId = authorId;
    }

    public Function(int id, String name, String type, int authorId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.authorId = authorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public boolean equals(Function obj) {
        return name.equals(obj.name) && type.equals(obj.type) && authorId == obj.authorId && id == obj.id;
    }
}