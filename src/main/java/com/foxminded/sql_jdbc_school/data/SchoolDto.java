package com.foxminded.sql_jdbc_school.data;

import java.util.List;

import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class SchoolDto {

    List<Student> students;
    List<Course> courses;
    List<Group> groups;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((courses == null) ? 0 : courses.hashCode());
        result = prime * result + ((groups == null) ? 0 : groups.hashCode());
        result = prime * result + ((students == null) ? 0 : students.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SchoolDto other = (SchoolDto) obj;
        if (courses == null) {
            if (other.courses != null)
                return false;
        } else if (!courses.equals(other.courses))
            return false;
        if (groups == null) {
            if (other.groups != null)
                return false;
        } else if (!groups.equals(other.groups))
            return false;
        if (students == null) {
            if (other.students != null)
                return false;
        } else if (!students.equals(other.students))
            return false;
        return true;
    }

    public static class Builder {

        private SchoolDto newDto;

        public Builder() {
            this.newDto = new SchoolDto();
        }

        public Builder withStudents(List<Student> students) {
            newDto.students = students;
            return this;
        }

        public Builder withGroups(List<Group> groups) {
            newDto.groups = groups;
            return this;
        }

        public Builder withCourses(List<Course> courses) {
            newDto.courses = courses;
            return this;
        }

        public SchoolDto build() {
            return newDto;
        }
    }
}