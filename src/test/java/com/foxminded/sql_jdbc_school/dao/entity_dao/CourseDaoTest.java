package com.foxminded.sql_jdbc_school.dao.entity_dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Course;

@TestInstance(Lifecycle.PER_CLASS)
class CourseDaoTest {
    
    CourseDao courseDao = CourseDao.getInstance();

    @BeforeEach
    private void prepareTables() {
        TablesCreation creation = new TablesCreation();
        creation.create();
    }
    
    @Test
    void getAll_shouldWorkCorrectly() throws SQLException {
        List<Course> expected = retriveCourses();
        expected.get(0).setId(1);
        expected.get(1).setId(2);
        expected.get(2).setId(3);
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO courses
                    (id, course_name, course_description)
                    VALUES
                    (1, 'testName0', 'testDescription'),
                    (2, 'testName1', 'testDescription'),
                    (3, 'testName2', 'testDescription');""");
        }
        List<Course> groups = courseDao.getAll();
        assertEquals(expected, groups);
    }
    
    @Test
    void saveAllshouldWorkCorrectly() {
        List<Course> testCourses = retriveCourses();
        courseDao.saveAll(testCourses);
        assertEquals(testCourses, courseDao.getAll());
        
    }
    
    @Test
    void getById_shoulsWorcCorrectly() {
        List<Course> testCourses = insertCourses();
        Course course = testCourses.get(1);
        assertEquals(course, courseDao.getById(course.getId()).get()); 
    }
    
    @Test
    void save_shouldWorkCorrectly() {
        Course course = new Course("testName", "testDescription");
        courseDao.save(course);
        assertEquals(course, courseDao.getById(course.getId()).get());
    }
    
    @Test
    void update_shouldWorkCorrectly() {
        Course course = new Course("testName", "testDescription");
        courseDao.save(course);
        course.setName("changed");
        course.setName("changed");
        courseDao.update(course);
        assertEquals(course, courseDao.getById(course.getId()).get()); 
    }
    
    @Test
    void updateAll_shouldWorkCorrectly() {
        List<Course> testCourses = insertCourses();
        testCourses.forEach(s -> s.setDescription("changed"));
        courseDao.updateAll(testCourses);
        assertEquals(testCourses, courseDao.getAll());
    }
    
    @Test
    void getCoursesByStudentId_shouldWorkCorrectly() throws SQLException {
        List<Course> courses = insertCourses();
        insertTestStudents();
        addstudentsToCourses();
        List<Course> coursesOfStudentId1 = new ArrayList<>(courses);
        List<Course> coursesOfStudentId2 = Arrays.asList(courses.get(0), courses.get(1));
        List<Course> coursesOfStudentId3 = Arrays.asList(courses.get(1));
        assertEquals(coursesOfStudentId1, courseDao.getCoursesByStudentId(1));
        assertEquals(coursesOfStudentId2, courseDao.getCoursesByStudentId(2));
        assertEquals(coursesOfStudentId3, courseDao.getCoursesByStudentId(3));  
    }
    
    @Test 
    void getCoursesStudentDoesNotHave_shouldWorkCorrectly() throws SQLException{
        List<Course> courses = insertCourses();
        insertTestStudents();
        addstudentsToCourses();
        List<Course> coursesOfStudentId1 = new ArrayList<>();
        List<Course> coursesOfStudentId2 = Arrays.asList(courses.get(2));
        List<Course> coursesOfStudentId3 = Arrays.asList(courses.get(0), courses.get(2));
        assertEquals(coursesOfStudentId1, courseDao.getCoursesStudentDoesNotHave(1));
        assertEquals(coursesOfStudentId2, courseDao.getCoursesStudentDoesNotHave(2));
        assertEquals(coursesOfStudentId3, courseDao.getCoursesStudentDoesNotHave(3));

    }
    
    private List<Course> insertCourses() {
        return courseDao.saveAll(retriveCourses());
    }

    private List<Course> retriveCourses() {
        List<Course> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            result.add(new Course("testName" + i, "testDescription"));
        }
        return result;
    }
    
    private void insertTestStudents() throws SQLException {
        String sql = """
                INSERT INTO students
                (group_id, first_name, last_name)
                VALUES
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test'),
                (null, 'test', 'test');""";
        
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        }
    }
    
    private void addstudentsToCourses() throws SQLException {
        String sql = """
                INSERT INTO students_courses
                (student_id, course_id)
                VALUES
                (1, 1),
                (1, 2),
                (1, 3),
                (2, 1),
                (2, 2),
                (3, 2),
                (4, 1),
                (5, 3),
                (6, 2),
                (7, 2),
                (8, 2),
                (9, 2),
                (10, 2);""";
        
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}