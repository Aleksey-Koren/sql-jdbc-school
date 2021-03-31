package com.foxminded.sql_jdbc_school.menu.terminal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.dao.entity_dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.Processor;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.ToStringFormatter;
import com.foxminded.sql_jdbc_school.dto.MenuDto;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    
    private Processor processor = new Processor(new ToStringFormatter());
    
    @BeforeEach
    private void prepareTables() {
        TablesCreation creation = new TablesCreation();
        creation.create();
    }
    
    @Test
    void requestToUser_shouldReturnUserInput() throws IOException{
        String expected = "expected response";
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(expected);
        assertEquals(expected, processor.requestToUser(reader, "prompt"));
    }
    
    @Test
    void requestToUser_shouldPrintPromptToConsole() throws IOException {
        PrintStream defaultOutput = System.out;
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        PrintStream outputWrapper = new PrintStream(testOutput);
        System.setOut(outputWrapper);
        String expected = "expected response";
        String prompt = "prompt";
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(expected);
        processor.requestToUser(reader, prompt);
        assertEquals(prompt + "\r\n", testOutput.toString());
        System.setOut(defaultOutput);
    }
    
    @Test
    void processAddNewStudent_shouldWorkCorrectly() throws IOException {
        Student forCaptor = new Student("testname", "testname");
        Student toReturn = new Student(1, null, "testname", "testname");
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        StudentDao studentDao = Mockito.mock(StudentDao.class);

        InOrder inOrder = Mockito.inOrder(reader, studentDao);
        Mockito.when(studentDao.save(forCaptor)).thenReturn(toReturn);
        Mockito.when(reader.readLine()).thenReturn("testname");
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        MenuDto dto = processor.processAddNewStudent(reader, studentDao);
        inOrder.verify(reader, Mockito.times(2)).readLine();
        inOrder.verify(studentDao).save(captor.capture());
        Student student = dto.getStudent().get();
        assertEquals(forCaptor, captor.getValue());
    }
    
    @Test
    void deleteById_shouldWorkCorrectly() throws SQLException, IOException {
        int expectedId = 1;
        Student expectedStudent = new Student(expectedId, null, "firstName", "lastName");
        insertStudent();
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedId));
        StudentDao studentDao = Mockito.spy(StudentDao.getInstance());
        MenuDto dto = processor.processDeleteById(reader, studentDao);
        InOrder inOrder = Mockito.inOrder(reader, studentDao);
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(studentDao).deleteById(expectedId);
        assertEquals(expectedStudent, dto.getStudent().get());      
    }


    
    @Test
    void processAddStudentToCourse_shouldWorkCorrectly() throws SQLException, IOException {
        Integer expectedId = 1;
        insertStudent();
        insertCourse();
        Student expectedStudent = new Student(expectedId, null , "firstName", "lastName");
        Course expecCourse = new Course(expectedId, "testName0", "testDescription");
        Set<Integer> expectedSet = new HashSet<>();
        expectedSet.add(expectedId);
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedId));
        StudentDao studentDao = Mockito.spy(StudentDao.getInstance());
        CourseDao courseDao  = Mockito.spy(CourseDao.getInstance());        
        InOrder inOrder = Mockito.inOrder(reader, studentDao, courseDao);
                     
        MenuDto dto = processor.processAddStudentCourse(reader, studentDao, courseDao);
        
        assertEquals(expectedStudent, dto.getStudent().get());
        assertEquals(expecCourse, dto.getCourse().get());
        
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(studentDao).getById(expectedId);
        inOrder.verify(courseDao).getCoursesStudentDoesNotHave(expectedId);
        inOrder.verify(reader).readLine();
        inOrder.verify(courseDao).getById(expectedId);
        inOrder.verify(studentDao).addStudentToCourses(expectedId, expectedSet);
    }
    
    @Test
    void processDeleteStudentFromCourse_shouldWorkCorrectly() throws SQLException, IOException {
        Integer expectedId = 1;
        insertStudent();
        insertCourse();
        addStudentToCourse();
        Student expectedStudent = new Student(expectedId, null , "firstName", "lastName");
        Course expecCourse = new Course(expectedId, "testName0", "testDescription");
                
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedId));
        StudentDao studentDao = Mockito.spy(StudentDao.getInstance());
        CourseDao courseDao  = Mockito.spy(CourseDao.getInstance());        
        InOrder inOrder = Mockito.inOrder(reader, studentDao, courseDao);
        
        MenuDto dto = processor.processDeleteStudentFromCourse(reader, studentDao, courseDao);
                
        assertEquals(expectedStudent, dto.getStudent().get());
        assertEquals(expecCourse, dto.getCourse().get());
        
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(studentDao).getById(expectedId);
        inOrder.verify(courseDao).getCoursesByStudentId(expectedId);
        inOrder.verify(reader).readLine();
        inOrder.verify(courseDao).getById(expectedId);
        inOrder.verify(studentDao).deleteStudentFromCourse(expectedId, expectedId);
    }
    
    @Test
    void processGetStudentsByCourse_shouldWorkCorrectly() throws SQLException, IOException {
        Integer expectedId = 1;
        String expectedCourseName = "testName0";
        insertTestStudents();
        insertTestCourses();
        addStudentsToCourses();
        List<Student> expectedStudents = Arrays.asList(new Student(1, null, "test", "test"),
                                                       new Student(2, null, "test", "test"),
                                                       new Student(4, null, "test", "test"));
        
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedId));
        StudentDao studentDao = Mockito.spy(StudentDao.getInstance());
        CourseDao courseDao  = Mockito.spy(CourseDao.getInstance());        
        InOrder inOrder = Mockito.inOrder(reader, studentDao, courseDao);
        
        MenuDto dto = processor.processGetStudentsByCourse(reader, studentDao, courseDao);
        
        List<Student> students = dto.getStudents();
        assertEquals(expectedStudents, students);
        
        inOrder.verify(courseDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(courseDao).getById(expectedId);
        inOrder.verify(studentDao).getAllByCourseName(expectedCourseName);    
    }
    
    private void addStudentToCourse() throws SQLException {
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO students_courses 
                    (student_id, course_id)
                    VALUES
                    (1, 1);""");
        } 
    }

    private void insertStudent() throws SQLException {
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO students 
                    (group_id, first_name, last_name)
                    VALUES
                    (NULL, 'firstName', 'lastName');""");
        }       
    }

    private void insertCourse() throws SQLException {
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO courses
                    (course_name, course_description)
                    VALUES
                    ('testName0', 'testDescription');""");
        }    
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
    
    private void insertTestCourses() throws SQLException {
        
        String sql = """
                INSERT INTO courses
                (id, course_name, course_description)
                VALUES
                (1, 'testName0', 'testDescription'),
                (2, 'testName1', 'testDescription'),
                (3, 'testName2', 'testDescription');""";
        
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        }    
    }
    
    private void addStudentsToCourses() throws SQLException {
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
