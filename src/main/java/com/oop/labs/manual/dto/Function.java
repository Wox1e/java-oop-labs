package com.oop.labs.manual.dto;

public class Function {
    private long id;
    private String name;
    private String type;
    private long authorId;

    public Function(String name, String type, long authorId) {
        this.name = name;
        this.type = type;
        this.authorId = authorId;
    }

    public Function(long id, String name, String type, long authorId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.authorId = authorId;
    }

    public long getId() {
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

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public boolean equals(Function obj) {
        return name.equals(obj.name) && type.equals(obj.type) && authorId == obj.authorId && id == obj.id;
    }
}