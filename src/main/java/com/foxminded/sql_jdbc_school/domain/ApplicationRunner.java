package com.foxminded.sql_jdbc_school.domain;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.domain.data_generation.CoursesFromFile;
import com.foxminded.sql_jdbc_school.domain.data_generation.DataProvider;
import com.foxminded.sql_jdbc_school.domain.data_generation.EntitiesGeneration;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomDataProvider;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomGroups;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomStudents;
import com.foxminded.sql_jdbc_school.domain.data_generation.RandomAssignment;

public class ApplicationRunner {
    
    public static void main(String[] args) {
        TablesCreation tablesCreator = new TablesCreation();
        tablesCreator.create();
       
        EntitiesGeneration entities = new EntitiesGeneration(new RandomStudents(),
                                                             new CoursesFromFile(),
                                                             new RandomGroups());
        RandomAssignment assignment = new RandomAssignment();
        DataProvider dataProvider = new RandomDataProvider(entities, assignment);
        dataProvider.provide();
    }
}