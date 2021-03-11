package com.foxminded.sql_jdbc_school.domain.entity;

public class Course {
    
    private Integer id;
    private String name;
    private String description;
        
    public Course(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Course(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Course [id=" + id + ", name=" + name + ", description=" + description + "]";
    }
    
    
}