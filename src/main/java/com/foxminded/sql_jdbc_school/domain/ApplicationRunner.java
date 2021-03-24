package com.foxminded.sql_jdbc_school.domain;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.domain.data_generation.CourseGenerator;
import com.foxminded.sql_jdbc_school.domain.data_generation.DataProvider;
import com.foxminded.sql_jdbc_school.domain.data_generation.EntityGeneration;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomDataProvider;
import com.foxminded.sql_jdbc_school.domain.data_generation.GroupsGenerator;
import com.foxminded.sql_jdbc_school.domain.data_generation.StudentsGenerator;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.Formatter;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.TerminalMenu;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.Processor;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.ToStringFormatter;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomAssignment;

public class ApplicationRunner {
    
    
    public static void main(String[] args) {
        TablesCreation tablesCreator = new TablesCreation();
        tablesCreator.create();

        EntityGeneration<Student> studentsGenerator = new StudentsGenerator();
        EntityGeneration<Group> groupsGenerator = new GroupsGenerator();
        EntityGeneration<Course> coursesGenerator = new CourseGenerator();
        
        RandomAssignment assignment = new RandomAssignment();
        DataProvider dataProvider = new RandomDataProvider(studentsGenerator,
                                                           groupsGenerator,
                                                           coursesGenerator,
                                                           assignment);
        dataProvider.provide();
        
        Formatter formatter = new ToStringFormatter();
        Processor processor = new Processor(formatter);
        TerminalMenu terminalMenu = new TerminalMenu(processor, formatter);
        terminalMenu.run();
    }
}