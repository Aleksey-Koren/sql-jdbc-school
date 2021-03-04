package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.util.List;

import com.foxminded.sql_jdbc_school.data.SchoolDto;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class SchoolEntitiesCreation {
    
    private EntityCreation<Student> studentsCreation;
    private EntityCreation<Course> coursesCreation;
    private EntityCreation<Group> groupsCreation;
    
    public SchoolEntitiesCreation(EntityCreation<Student> studentsCreation,
                                  EntityCreation<Course> coursesCreation,
                                  EntityCreation<Group> groupsCreation) {
        
        this.studentsCreation = studentsCreation;
        this.coursesCreation = coursesCreation;
        this.groupsCreation = groupsCreation;
    }

    public SchoolDto init() throws Exception {
        List<Student> students  = studentsCreation.create();
        List<Course> courses = coursesCreation.create();
        List<Group> groups = groupsCreation.create();
        
        
        
        return new SchoolDto.Builder()
                .withCourses(courses)
                .withGroups(groups)
                .withStudents(students)
                .build();
    }
}