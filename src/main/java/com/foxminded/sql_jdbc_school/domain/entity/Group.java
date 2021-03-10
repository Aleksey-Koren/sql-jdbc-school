package com.foxminded.sql_jdbc_school.domain.entity;

public class Group {

    private Integer id;
    private String name;
        
    public Group(String name) {
        this.name = name;
    }
    
    public Group(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Group [id=" + id + ", name=" + name + "]";
    }
}