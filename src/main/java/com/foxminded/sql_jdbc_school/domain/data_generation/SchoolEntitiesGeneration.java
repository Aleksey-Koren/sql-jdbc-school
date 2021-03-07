package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.util.List;

import com.foxminded.sql_jdbc_school.data.SchoolDto;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class SchoolEntitiesGeneration {
    
    private EntityGeneration<Student> studentsGeneration;
    private EntityGeneration<Course> coursesGeneration;
    private EntityGeneration<Group> groupsGeneration;
    
    public SchoolEntitiesGeneration(EntityGeneration<Student> studentsGeneration,
                                  EntityGeneration<Course> coursesGeneration,
                                  EntityGeneration<Group> groupsGeneration) {
        
        this.studentsGeneration = studentsGeneration;
        this.coursesGeneration = coursesGeneration;
        this.groupsGeneration = groupsGeneration;
    }

    public SchoolDto generate() throws Exception  {
        List<Student> students  = studentsGeneration.generate();
        List<Course> courses = coursesGeneration.generate();
        List<Group> groups = groupsGeneration.generate();
        
        
        
        return new SchoolDto.Builder()
                .withCourses(courses)
                .withGroups(groups)
                .withStudents(students)
                .build();
    }
}