package com.foxminded.sql_jdbc_school.dto;

import java.util.List;
import java.util.Optional;

import com.foxminded.sql_jdbc_school.domain.DomainException;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class MenuDto  {
    
    private boolean isCanceled;
    private boolean isSelected;
    private Optional<Student> student;
    private List<Student> students;
    private Optional<Course> course;
    private List<Course> courses;
    private List<Group> groups;
    String newName;
    
    public <T> void setEntity(Optional<T> entity){
        if (entity.get() instanceof Student) {
            this.student = (Optional<Student>) entity;
        }else if (entity.get() instanceof Course) {
            this.course = (Optional<Course>) entity;
        }else {
            throw new DomainException("Invalid entity generic type");
        }
    }
    
    
    
    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public boolean isCanceled() {
        return isCanceled;
    }
    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }
    public Optional<Student> getStudent() {
        return student;
    }
    public void setStudent(Optional<Student> student) {
        this.student = student;
    }
    public List<Student> getStudents() {
        return students;
    }
    public void setStudents(List<Student> students) {
        this.students = students;
    }
    public Optional<Course> getCourse() {
        return course;
    }
    public void setCourse(Optional<Course> course) {
        this.course = course;
    }
    public List<Course> getCourses() {
        return courses;
    }
    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
    public String getNewName() {
        return newName;
    }
    public void setNewName(String newName) {
        this.newName = newName;
    }
    public List<Group> getGroups() {
        return groups;
    }
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}