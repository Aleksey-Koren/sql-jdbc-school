package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.sql.SQLException;

import com.foxminded.sql_jdbc_school.dao.entity_dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.GroupDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.dto.SchoolDto;


public class RandomDataProvider implements DataProvider {
    
    private EntitiesGeneration entities;
    private RandomAssignment assignment;
    private StudentDao studentDao = StudentDao.getInstance(); 
    private GroupDao groupDao = GroupDao.getInstance();
    CourseDao courseDao = CourseDao.getInstance();
    
    public RandomDataProvider(EntitiesGeneration entities, RandomAssignment assignment) {
        this.entities = entities;
        this.assignment = assignment;
    }

    public void provide() throws SQLException { 
        SchoolDto dto = entities.generate();
        dto.setStudents(studentDao.saveAll(dto.getStudents()));
        dto.setGroups(groupDao.saveAll(dto.getGroups()));
        dto.setCourses(courseDao.saveAll(dto.getCourses()));
        dto.setStudents(assignment.assignGroups(dto));
        assignment.assignCourses(dto);
    }
}