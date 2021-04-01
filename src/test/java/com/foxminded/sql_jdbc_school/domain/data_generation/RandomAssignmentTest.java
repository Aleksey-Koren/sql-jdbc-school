package com.foxminded.sql_jdbc_school.domain.data_generation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.dao.entity_dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.GroupDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.dao.util.PropertiesUtil;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.dto.SchoolDto;

@TestInstance(Lifecycle.PER_CLASS)
class RandomAssignmentTest {
    
    private static final int MIN_GROUP_SIZE =
            Integer.parseInt(PropertiesUtil.get("min.group.size"));
    private static final int MAX_GROUP_SIZE =
            Integer.parseInt(PropertiesUtil.get("max.group.size"));
    private static final int MIN_COURSE_QUANTITY =
            Integer.parseInt(PropertiesUtil.get("min.course.quantity"));
    private static final int MAX_COURSE_QUANTITY =
            Integer.parseInt(PropertiesUtil.get("max.course.quantity"));
    
    private StudentDao studentDao = StudentDao.getInstance();
    private GroupDao groupDao = GroupDao.getInstance();
    private CourseDao courseDao = CourseDao.getInstance();
    
    private static final String COUNT_STUDENTS_BY_GROUP_ID = """
            SELECT count(id) students_quantity
            FROM students
            WHERE group_id = ?
            GROUP BY group_id;""";
    
    private static final String COUNT_COURSES_OF_STUDENTS = """
            SELECT  student_id, count(course_id) count
            FROM students_courses
            GROUP BY student_id;""";
    
    @BeforeAll
    private void createTables() {
        TablesCreation creation = new TablesCreation();
        creation.create();
    }
    
    private RandomAssignment assignment = new RandomAssignment();
    
    @Test 
    void assignGroups_shouldAssignGroupsAccordingParameters() throws SQLException {
        SchoolDto dto = prepareDto();
        studentDao.saveAll(dto.getStudents());
        groupDao.saveAll(dto.getGroups());
        
        assertEquals(0, countStudentsByGroupId(1));
        assertEquals(0, countStudentsByGroupId(2));
        
        assignment.assignGroups(dto);
        
        assertTrue((countStudentsByGroupId(1) >= MIN_GROUP_SIZE) && 
                   (countStudentsByGroupId(1) <= MAX_GROUP_SIZE));
        assertTrue((countStudentsByGroupId(2) >= MIN_GROUP_SIZE) && 
                (countStudentsByGroupId(2) <= MAX_GROUP_SIZE));
    }
    
    @Test
    void assignCourses_shouldAssignCoursesCorrectly() throws SQLException {
        String testName1 = "math";
        String testName2 = "biology"; 
        courseDao.saveAll(retriveCourses(testName1, testName2));
        
        SchoolDto dto = new SchoolDto.Builder()
                                     .withStudents(studentDao.getAll())
                                     .withGroups(groupDao.getAll())
                                     .withCourses(courseDao.getAll())
                                     .build();
        
        try(Connection connection = ConnectionManager.get();
                PreparedStatement countStatement = connection.prepareStatement(COUNT_COURSES_OF_STUDENTS)) {
                ResultSet resultSet = countStatement.executeQuery();
                assertFalse(resultSet.next());
            }
        
        assignment.assignCourses(dto);
        
        try(Connection connection = ConnectionManager.get();
                PreparedStatement countStatement = connection.prepareStatement(COUNT_COURSES_OF_STUDENTS)) {
                ResultSet resultSet = countStatement.executeQuery();
                assertTrue(resultSet.next());
                do {
                    Integer count = resultSet.getInt("count");
                    assertTrue((count >= MIN_COURSE_QUANTITY) 
                            &&(count <= MAX_COURSE_QUANTITY));
                }while(resultSet.next());
            }
    }

    private int countStudentsByGroupId(Integer id) throws SQLException {
        int count = 0;
        try(Connection connection = ConnectionManager.get();
            PreparedStatement countStatement = connection.prepareStatement(COUNT_STUDENTS_BY_GROUP_ID)) {
            countStatement.setInt(1, id);
            ResultSet resultSet = countStatement.executeQuery();
            if(resultSet.next()) {
                count = resultSet.getInt("students_quantity");
            }
        }
        return count;
    }
    
    private SchoolDto prepareDto() {
        return new SchoolDto.Builder()
                            .withStudents(retriveStudents())
                            .withGroups(retriveGroups())
                            .build();
    }

    private List<Student> retriveStudents() {
        List<Student> students = new ArrayList<>(5);
        for (int i = 1; i <= 20; i++) {
            students.add(new Student(i, null, "Ivan", "Ivanov"));
        }

        return students;
    }
    
    private List<Group> retriveGroups() {
        return Arrays.asList(new Group("testGroup1"), new Group("testGroup2"));
    }
    
    List<Course> retriveCourses(String name1, String name2) {
        Course course1 = new Course(name1, "Test description");
        Course course2 = new Course(name2, "Test description");
        List<Course> courses = Arrays.asList(course1, course2);
        return courses;
    }
}