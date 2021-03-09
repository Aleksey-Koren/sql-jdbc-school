package com.foxminded.sql_jdbc_school.domain;




import com.foxminded.sql_jdbc_school.dao.SchoolTablesCreation;
import com.foxminded.sql_jdbc_school.dao.entity_dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.GroupDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.data.SchoolDto;
import com.foxminded.sql_jdbc_school.domain.data_generation.CoursesGeneration;
import com.foxminded.sql_jdbc_school.domain.data_generation.GroupsGeneration;
import com.foxminded.sql_jdbc_school.domain.data_generation.SchoolEntitiesGeneration;
import com.foxminded.sql_jdbc_school.domain.data_generation.StudentsAssignment;
import com.foxminded.sql_jdbc_school.domain.data_generation.StudentsGeneration;


public class RandomDataRunner {
    
    public void run() throws Exception  {
        SchoolTablesCreation tablesCreator = new SchoolTablesCreation();

        SchoolEntitiesGeneration entityCreation = 
                new SchoolEntitiesGeneration(new StudentsGeneration(),
                                                 new CoursesGeneration(),
                                                 new GroupsGeneration());
        StudentsAssignment assignment = new StudentsAssignment();
        StudentDao studentDao = StudentDao.getInstance();
        GroupDao groupDao = GroupDao.getInstance();
        CourseDao courseDao = CourseDao.getInstance();
        
        tablesCreator.create();
        
        SchoolDto dto = entityCreation.generate();
        
        dto.setStudents(studentDao.saveAll(dto.getStudents()));
        dto.setGroups(groupDao.saveAll(dto.getGroups()));
        dto.setCourses(courseDao.saveAll(dto.getCourses()));

        dto.setStudents(assignment.assignGroups(dto));
        studentDao.updateAll(dto.getStudents());
        assignment.assignCourses(dto);
    }
    
    
//    public static void main(String[] args) throws Exception {
//        RandomDataRunner.run();
//    }
}