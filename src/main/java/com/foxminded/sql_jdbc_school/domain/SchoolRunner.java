package com.foxminded.sql_jdbc_school.domain;


import com.foxminded.sql_jdbc_school.dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.GroupDao;
import com.foxminded.sql_jdbc_school.dao.SchoolTablesCreation;
import com.foxminded.sql_jdbc_school.dao.StudentDao;
import com.foxminded.sql_jdbc_school.data.SchoolDto;
import com.foxminded.sql_jdbc_school.domain.data_generation.CoursesCreation;
import com.foxminded.sql_jdbc_school.domain.data_generation.GroupsCreation;
import com.foxminded.sql_jdbc_school.domain.data_generation.SchoolEntitiesCreation;
import com.foxminded.sql_jdbc_school.domain.data_generation.StudentsCreation;

public class SchoolRunner {
    
    public static void run() throws Exception {
        SchoolTablesCreation tablesCreator = new SchoolTablesCreation();
        tablesCreator.create();
        SchoolEntitiesCreation entityCreation = 
                new SchoolEntitiesCreation(new StudentsCreation(),
                                                 new CoursesCreation(),
                                                 new GroupsCreation());
        SchoolDto dto = entityCreation.init();
        StudentDao studentDao = StudentDao.getInstance();
        studentDao.saveAll(dto.getStudents());
        GroupDao groupDao = GroupDao.getInstance();
        groupDao.saveAll(dto.getGroups());
        CourseDao courseDao = CourseDao.getInstance();
        courseDao.saveAll(dto.getCourses());    
    }
    
    public static void main(String[] args) throws Exception {
        SchoolRunner.run();
    }
}