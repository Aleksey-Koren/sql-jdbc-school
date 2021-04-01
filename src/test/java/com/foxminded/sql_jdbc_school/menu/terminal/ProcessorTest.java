package com.foxminded.sql_jdbc_school.menu.terminal;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foxminded.sql_jdbc_school.dao.entity_dao.CourseDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.GroupDao;
import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.Processor;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.ToStringFormatter;
import com.foxminded.sql_jdbc_school.dto.MenuDto;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {
    
    private Processor processor = new Processor(new ToStringFormatter());

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
        Student expectedForCaptor = new Student("testname", "testname");
        Student toReturn = new Student(1, null, "testname", "testname");
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        StudentDao studentDao = Mockito.mock(StudentDao.class);
        InOrder inOrder = Mockito.inOrder(reader, studentDao);
        Mockito.when(studentDao.save(expectedForCaptor)).thenReturn(toReturn);
        Mockito.when(reader.readLine()).thenReturn("testname");
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        
        MenuDto dto = processor.processAddNewStudent(reader, studentDao);
        
        inOrder.verify(reader, Mockito.times(2)).readLine();
        inOrder.verify(studentDao).save(captor.capture());
        
        assertEquals(expectedForCaptor, captor.getValue());
        assertSame(toReturn, dto.getStudent().get());
        assertFalse(dto.isCanceled());
        
        Mockito.when(reader.readLine()).thenReturn("back");
        
        MenuDto canseledDTO = processor.processAddNewStudent(reader, studentDao);
        
        inOrder.verify(reader).readLine();
        inOrder.verifyNoMoreInteractions();
        
        assertTrue(canseledDTO.isCanceled());
    }
    
    @Test
    void processDeleteById_shouldWorkCorrectly() throws SQLException, IOException {
        List<Student> expectedStudents = retriveTestStudents();
        int expectedStudentId = expectedStudents.get(0).getId();
        Student expectedStudent = expectedStudents.get(0);
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedStudentId));
        StudentDao studentDao = Mockito.mock(StudentDao.class);
        Mockito.when(studentDao.getAll()).thenReturn(expectedStudents);
        Mockito.when(studentDao.getById(expectedStudentId)).thenReturn(Optional.of(expectedStudent));
        Mockito.when(studentDao.deleteById(expectedStudent.getId())).thenReturn(true);
        
        MenuDto dto = processor.processDeleteById(reader, studentDao);
        
        InOrder inOrder = Mockito.inOrder(reader, studentDao);
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(studentDao).getById(expectedStudentId);
        inOrder.verify(studentDao).deleteById(expectedStudentId);
        inOrder.verifyNoMoreInteractions();
        
        assertSame(expectedStudent, dto.getStudent().get());
        assertFalse(dto.isCanceled());
        
        Mockito.when(reader.readLine()).thenReturn("back");
        
        MenuDto canseledDTO = processor.processDeleteById(reader, studentDao);
        
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verifyNoMoreInteractions();
        
        assertTrue(canseledDTO.isCanceled());
    }
    
    @Test
    void processAddStudentToCourse_shouldWorkCorrectly() throws SQLException, IOException {
        List<Student> expectedStudents = retriveTestStudents();
        Student expectedStudent = expectedStudents.get(0);
        Integer expectedStudentId = expectedStudent.getId();
        List<Course> expectedCourses = retriveTestCourses();
        Course expectedCourse = expectedCourses.get(1);
        Integer expectedCourseId = expectedCourse.getId();
        Set<Integer> expectedSet = new HashSet<>();
        expectedSet.add(expectedCourseId);
        
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        StudentDao studentDao = Mockito.mock(StudentDao.class);
        CourseDao courseDao  = Mockito.mock(CourseDao.class);
        
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedStudentId), 
                String.valueOf(expectedCourseId));
        Mockito.when(studentDao.getAll()).thenReturn(expectedStudents);
        Mockito.when(studentDao.getById(expectedStudentId)).thenReturn(Optional.of(expectedStudent));
        Mockito.when(courseDao.getCoursesStudentDoesNotHave(expectedStudentId))
                                    .thenReturn(expectedCourses);
        Mockito.when(courseDao.getById(expectedCourseId)).thenReturn(Optional.of(expectedCourse));
        
        MenuDto dto = processor.processAddStudentToCourse(reader, studentDao, courseDao);
        
        assertSame(expectedStudent, dto.getStudent().get());
        assertSame(expectedCourse, dto.getCourse().get());
        assertFalse(dto.isCanceled());
        
        InOrder inOrder = Mockito.inOrder(reader, studentDao, courseDao);
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(studentDao).getById(expectedStudentId);
        inOrder.verify(courseDao).getCoursesStudentDoesNotHave(expectedStudentId);
        inOrder.verify(reader).readLine();
        inOrder.verify(courseDao).getById(expectedCourseId);
        inOrder.verify(studentDao).addStudentToCourses(expectedStudentId, expectedSet);
        
        Mockito.when(reader.readLine()).thenReturn("back");
        
        MenuDto canseledDTO = processor.processAddStudentToCourse(reader, studentDao, courseDao);
        
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verifyNoMoreInteractions();
        
        assertTrue(canseledDTO.isCanceled());
    }
    
    @Test
    void processDeleteStudentFromCourse_shouldWorkCorrectly() throws SQLException, IOException {
        List<Student> expectedStudents = retriveTestStudents();
        Student expectedStudent = expectedStudents.get(0);
        Integer expectedStudentId = expectedStudent.getId();
        List<Course> expectedCourses = retriveTestCourses();
        Course expectedCourse = expectedCourses.get(1);
        Integer expectedCourseId = expectedCourse.getId();
        
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        StudentDao studentDao = Mockito.mock(StudentDao.class);
        CourseDao courseDao  = Mockito.mock(CourseDao.class);
        
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedStudentId), 
                String.valueOf(expectedCourseId));
        Mockito.when(studentDao.getAll()).thenReturn(expectedStudents);
        Mockito.when(studentDao.getById(expectedStudentId)).thenReturn(Optional.of(expectedStudent));
        Mockito.when(courseDao.getCoursesByStudentId(expectedStudentId))
                                    .thenReturn(expectedCourses);
        Mockito.when(courseDao.getById(expectedCourseId)).thenReturn(Optional.of(expectedCourse));
        Mockito.when(studentDao.deleteStudentFromCourse(expectedStudentId, expectedCourseId))
                                    .thenReturn(true);
        
        InOrder inOrder = Mockito.inOrder(reader, studentDao, courseDao);
        
        MenuDto dto = processor.processDeleteStudentFromCourse(reader, studentDao, courseDao);
                
        assertSame(expectedStudent, dto.getStudent().get());
        assertSame(expectedCourse, dto.getCourse().get());
        assertFalse(dto.isCanceled());
        
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(studentDao).getById(expectedStudentId);
        inOrder.verify(courseDao).getCoursesByStudentId(expectedStudentId);
        inOrder.verify(reader).readLine();
        inOrder.verify(courseDao).getById(expectedCourseId);
        inOrder.verify(studentDao).deleteStudentFromCourse(expectedStudentId, expectedCourseId);
        
        Mockito.when(reader.readLine()).thenReturn("back");
        
        MenuDto canseledDTO = processor.processDeleteStudentFromCourse(reader, studentDao, courseDao);
        
        inOrder.verify(studentDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verifyNoMoreInteractions();
        
        assertTrue(canseledDTO.isCanceled());
    }
    
    @Test
    void processGetStudentsByCourse_shouldWorkCorrectly() throws SQLException, IOException {
        List<Student> expectedStudents = retriveTestStudents();
        List<Course> expectedCourses = retriveTestCourses();
        Course expectedCourse = expectedCourses.get(1);
        Integer expectedCourseId = expectedCourse.getId();
        
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        StudentDao studentDao = Mockito.mock(StudentDao.class);
        CourseDao courseDao  = Mockito.mock(CourseDao.class);
        
        Mockito.when(reader.readLine()).thenReturn(String.valueOf(expectedCourseId));
        Mockito.when(courseDao.getAll()).thenReturn(expectedCourses);
        Mockito.when(courseDao.getById(expectedCourseId)).thenReturn(Optional.of(expectedCourse));
        Mockito.when(studentDao.getAllByCourseName(expectedCourse.getName()))
                                        .thenReturn(expectedStudents);
           
        InOrder inOrder = Mockito.inOrder(reader, studentDao, courseDao);
        
        MenuDto dto = processor.processGetStudentsByCourse(reader, studentDao, courseDao);
        
        assertSame(expectedStudents, dto.getStudents());
        assertFalse(dto.isCanceled());
        
        inOrder.verify(courseDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verify(courseDao).getById(expectedCourseId);
        inOrder.verify(studentDao).getAllByCourseName(expectedCourse.getName());
        
        Mockito.when(reader.readLine()).thenReturn("back");
        
        MenuDto canseledDTO = processor.processGetStudentsByCourse(reader, studentDao, courseDao);
        
        inOrder.verify(courseDao).getAll();
        inOrder.verify(reader).readLine();
        inOrder.verifyNoMoreInteractions();
        
        assertTrue(canseledDTO.isCanceled());
    }
    
    @Test
    void processFindGroupsByStudentCount_shouldWorkCorrectly() throws IOException {
        List<Group> expectedGroups = retriveTestGroups();
        Integer expectedStudentCount = 15;
        
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        GroupDao groupDao = Mockito.mock(GroupDao.class);
        
        Mockito.when(reader.readLine()).thenReturn("15");
        Mockito.when(groupDao.getAllByStudentsQuantity(expectedStudentCount))
                               .thenReturn(expectedGroups);
        
        InOrder inOrder = Mockito.inOrder(reader, groupDao);
        
        MenuDto dto = processor.processFindGroupsByStudentCount(reader, groupDao);
        
        inOrder.verify(reader).readLine();
        inOrder.verify(groupDao).getAllByStudentsQuantity(expectedStudentCount);
        inOrder.verifyNoMoreInteractions();
        
        assertSame(expectedGroups, dto.getGroups());
        
        Mockito.when(reader.readLine()).thenReturn("back");
        
        MenuDto canseledDTO = processor.processFindGroupsByStudentCount(reader, groupDao);
        inOrder.verify(reader).readLine();
        inOrder.verifyNoMoreInteractions();
        assertTrue(canseledDTO.isCanceled());
    }
    
    private List<Student> retriveTestStudents() {
        return Arrays.asList(new Student(1, null, "test", "test"),
                             new Student(2, null, "test", "test"),
                             new Student(4, null, "test", "test"));
    }
    
    private List<Course> retriveTestCourses() {
        return Arrays.asList(new Course(1, "testName1", "testDescription1"),
                             new Course(2, "testName2", "testDescription2"));
    }
    
    private List<Group> retriveTestGroups() {
        return Arrays.asList(new Group(1, "qw-23"),
                             new Group(2, "qr-53"),
                             new Group(3, "as-72"));
    }
}