package com.foxminded.sql_jdbc_school.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import com.foxminded.sql_jdbc_school.domain.menu.ApplicationMenu;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomAssignment;

public class ApplicationRunner {
    
    private static final String MENU = """
            Type [1] to find all groups with less or equals student count
            
            """;
    private static final String EXIT = "exit";
    
    public static void main(String[] args) throws IOException {
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
        
        runMenu();
    }
    
    private static void runMenu() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ApplicationMenu menu = new ApplicationMenu();
        System.out.print(MENU);
        while(!reader.readLine().equals(EXIT)) {
            System.out.print(MENU);
        }
    }
}