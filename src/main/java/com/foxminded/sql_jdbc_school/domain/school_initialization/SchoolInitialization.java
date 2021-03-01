package com.foxminded.sql_jdbc_school.domain.school_initialization;

import java.util.List;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.domain.entity.creation.EntityCreation;

public class SchoolInitialization {
    
    private TablesCreation databaseTables;
    private EntityCreation<Student> studentsCreation;
    private EntityCreation<Course> courses;
    private EntityCreation<Group> groups;
    
    public SchoolInitialization(TablesCreation databaseTables,
                                EntityCreation<Student> studentsCreation,
                                EntityCreation<Course> courses, EntityCreation<Group> groups) {
        
        this.databaseTables = databaseTables;
        this.studentsCreation = studentsCreation;
        this.courses = courses;
        this.groups = groups;
    }

    public void init() throws Exception {
        databaseTables.create();
        List<Student> students  = studentsCreation.create();
    }
}
