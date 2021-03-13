package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.util.List;

import com.foxminded.sql_jdbc_school.dao.entity_dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.EntityDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.GroupDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.dto.SchoolDto;


public class RandomDataProvider implements DataProvider {
    
    private EntityGeneration<Student> studentsGeneration;
    private EntityGeneration<Group> groupsGeneration;
    private EntityGeneration<Course> coursesGeneration;
    private RandomAssignment assignment;
    private EntityDao<Student, Integer> studentDao = StudentDao.getInstance(); 
    private EntityDao<Group, Integer> groupDao = GroupDao.getInstance();
    private EntityDao<Course, Integer> courseDao = CourseDao.getInstance();
 
public RandomDataProvider(EntityGeneration<Student> studentsGeneration,
                          EntityGeneration<Group> groupsGeneration,
                          EntityGeneration<Course> coursesGeneration,
                          RandomAssignment assignment) {
        this.studentsGeneration = studentsGeneration;
        this.groupsGeneration = groupsGeneration;
        this.coursesGeneration = coursesGeneration;
        this.assignment = assignment;
    }

    public void provide() { 
        List<Student> students  = studentsGeneration.generate();
        List<Course> courses = coursesGeneration.generate();
        List<Group> groups = groupsGeneration.generate();
               
        SchoolDto dto = new SchoolDto.Builder()
                .withCourses(courses)
                .withGroups(groups)
                .withStudents(students)
                .build();
        
        dto.setStudents(studentDao.saveAll(dto.getStudents()));
        dto.setGroups(groupDao.saveAll(dto.getGroups()));
        dto.setCourses(courseDao.saveAll(dto.getCourses()));
        dto.setStudents(assignment.assignGroups(dto));
        assignment.assignCourses(dto);
    }
}