package com.foxminded.sql_jdbc_school.dao.entity_dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

@TestInstance(Lifecycle.PER_CLASS)
class StudentDaoTest {
    
    private StudentDao studentDao = StudentDao.getInstance();
    private static final String GET_COURSES_OF_STUDENT = """
            SELECT student_id, course_id
            FROM students_courses
            WHERE student_id = ?;""";
    
    @BeforeEach
    private void prepareTables() {
        TablesCreation creation = new TablesCreation();
        creation.create();
    }

    @Test
    void getAll_shouldWorkCorrectly() throws SQLException { 
        List<Student> expected = Arrays.asList(new Student(1, null, "firstName1", "lastName1"),
                                               new Student(2, null, "firstName2", "lastName2"),
                                               new Student(3, 1, "firstName3", "lastName3"));
        insertGroup();
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO students 
                    (group_id, first_name, last_name)
                    VALUES
                    (NULL, 'firstName1', 'lastName1'),
                    (NULL, 'firstName2', 'lastName2'),
                    (1, 'firstName3', 'lastName3');""");
        }
        List<Student> students = studentDao.getAll();
        assertEquals(expected, students);
    }
    
    private void insertGroup() throws SQLException {
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO groups 
                    (group_name)
                    VALUES
                    ('testGroupName');""");
        }
    }
    
    @Test
    void saveAll_shouldWorckCorrectly() {
        List<Student> expected = insertStudents();     
        List<Student> students = studentDao.getAll();
        assertEquals(expected, students); 
    }
    
    @Test
    void save_shouldWorckCorrectly() {
        Student expected = insertStudent();
        List<Student> students = studentDao.getAll();
        assertEquals(expected, students.get(0));
    }
    
    @Test
    void getById_shouldWorkCorrectly() {
        List<Student> students = insertStudents();
        Student expected = students.get(1);
        Optional<Student> student = studentDao.getById(2);
        assertEquals(expected, student.get());
    }
    
    @Test
    void deleteById_shouldWorkCorrectly() {
        List<Student> expected = insertStudents();
        studentDao.deleteById(2);
        expected.remove(1);  
        List<Student> students = studentDao.getAll();
        assertEquals(expected, students);
    }
    
    @Test
    void update_shouldWorcCorrectly() {
        Student student = insertStudent();
        student.setFirstName("changedName");
        studentDao.update(student);
        Student updatedStudent = studentDao.getById(1).get();
        assertEquals(student, updatedStudent);
    }
    
    @Test 
    void updateAll_shouldWorkCorrectly() {
        List<Student> expected = insertStudents();
        expected.forEach(s ->  s.setFirstName("Changed"));
        studentDao.updateAll(expected);
        List<Student> students = studentDao.getAll();
        assertEquals(expected, students);
    }
    
    @Test
    void addStudentToCourses_shouldWorkCorrectly() throws SQLException {
        insertCourses();
        Student student = insertStudent();
        assertFalse(isStudentHasCourses(student));
        Set<Integer> courseId = retriveCourseIds();
        studentDao.addStudentToCourses(student.getId(), courseId);
        assertEquals(courseId, getCoursesIdOfStudent(student));          
    }
    
    private boolean isStudentHasCourses(Student student) throws SQLException {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(GET_COURSES_OF_STUDENT)){
            statement.setInt(1, student.getId());
            ResultSet resultSet = statement.executeQuery();     
            return resultSet.next();
        }
    }
        
    @Test
    void deleteStudentFromCourse_shouldWorkCorrectly() throws SQLException {
        insertCourses();
        Student student = insertStudent();
        Set<Integer> courseId = retriveCourseIds();
        studentDao.addStudentToCourses(student.getId(), courseId);
        courseId.remove(1);
        assertNotEquals(courseId, getCoursesIdOfStudent(student));
        studentDao.deleteStudentFromCourse(student.getId(), 1);
        assertEquals(courseId, getCoursesIdOfStudent(student));
    }
    
    @Test
    void getAllByCourseName_shouldWorkCorrectly() throws SQLException {
        List<Student> students = insertStudents();
        insertCourses();
        Set<Integer> courseId = retriveCourseIds();
        students.forEach(s -> studentDao.addStudentToCourses(s.getId(), courseId));
        assertEquals(students, studentDao.getAllByCourseName("Name1"));
    }
    
    private Set<Integer> getCoursesIdOfStudent(Student student) throws SQLException {
        Set<Integer> result = new HashSet<>();
        try(Connection connection = ConnectionManager.get();
                PreparedStatement statement = connection.prepareStatement(GET_COURSES_OF_STUDENT)){
            statement.setInt(1, student.getId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                result.add(resultSet.getInt("course_id"));
            }
        }
        return result;
    }
    
    private Student insertStudent() {
        Student student = new Student("firstName", "lastName");
        return studentDao.save(student);
    }
    
    private List<Student> insertStudents() {
        List<Student> result = new ArrayList<>(3);
        for(int i = 0; i < 3; i++) {
            result.add(new Student ("firstName", "lastName"));
        }
        return studentDao.saveAll(result);
    }
    
    private Set<Integer> retriveCourseIds() {
        Set<Integer> result = new HashSet<>();
        result.add(1);
        result.add(2);
        return result;
    }
    
    private void insertCourses() throws SQLException {
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO courses 
                    (course_name, course_description)
                    VALUES
                    ('Name1', 'Description1'),
                    ('Name2', 'Description2');""");
        }
    }
}