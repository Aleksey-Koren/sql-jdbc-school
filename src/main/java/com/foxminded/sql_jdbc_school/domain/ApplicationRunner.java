package com.foxminded.sql_jdbc_school.domain;

import java.sql.SQLException;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.domain.data_generation.CourseGenerator;
import com.foxminded.sql_jdbc_school.domain.data_generation.DataProvider;
import com.foxminded.sql_jdbc_school.domain.data_generation.EntityGeneration;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomDataProvider;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomGroupsGenerator;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomStudentsGenerator;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomAssignment;

public class ApplicationRunner {
    
    public static void main(String[] args) throws SQLException {
        TablesCreation tablesCreator = new TablesCreation();
        tablesCreator.create();

        EntityGeneration<Student> studentsGenerator = new RandomStudentsGenerator();
        EntityGeneration<Group> groupsGenerator = new RandomGroupsGenerator();
        EntityGeneration<Course> coursesGenerator = new CourseGenerator();
        
        RandomAssignment assignment = new RandomAssignment();
        DataProvider dataProvider = new RandomDataProvider(studentsGenerator,
                                                           groupsGenerator,
                                                           coursesGenerator,
                                                           assignment);
        dataProvider.provide();
    }
}