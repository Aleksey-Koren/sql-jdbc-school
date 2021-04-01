package com.foxminded.sql_jdbc_school.menu.terminal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;
import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.domain.menu.terminal.ToStringFormatter;
import com.foxminded.sql_jdbc_school.dto.MenuDto;

@TestInstance(Lifecycle.PER_CLASS)
class ToStringFormatterTest {
    
    private ToStringFormatter formatter = new ToStringFormatter();
    
    private List<Student> testStudents = retriveTestStudents();
    private Student testStudent = testStudents.get(0);
    private List<Course> testCourses = retriveTestCourses();
    private Course testCourse = testCourses.get(1);
    private List<Group> expectedGroups = retriveTestGroups();
    private MenuDto dto;

    @BeforeAll
    private void prepareDto() {
        MenuDto result = new MenuDto();
        result.setStudents(testStudents);
        result.setCourses(testCourses);
        result.setGroups(expectedGroups);
        result.setStudent(Optional.of(testStudent));
        result.setCourse(Optional.of(testCourse));
        dto = result;
    }
    
    @Test
    void formatAddStudent_shouldWorkCorrectly() {
        String expected = "Student 1 test test has been added";
        assertEquals(expected, formatter.formatAddStudent(dto));
    }

    @Test
    void formatDeleteStudentById_shouldWorkCorrectly() {
        String expected = "Student 1 test test has been deleted";
        assertEquals(expected, formatter.formatDeleteStudentById(dto));
    }
    
    @Test
    void formatAddStudentToCourse_shouldWorkCorrectly() {
        String expected = "Student 1 test test\n"
                        + "has been added to\n"
                        + "Course 2 testName2";
        assertEquals(expected, formatter.formatAddStudentToCourse(dto));
    }
    
    @Test
    void formatDeleteStudentFromCourse_shouldWorkCorrectly() {
        String expected = "Student 1 test test\n"
                        + "has been deleted from\n"
                        + "Course 2 testName2";
        assertEquals(expected, formatter.formatDeleteStudentFromCourse(dto));
    }
    
    @Test
    void formatGetStudentsByCourse_shouldWorkCorrectly() {
        String expected = "Course 2 testName2\n"
                        + "Students on this course:\n"
                        + "\n"
                        + "Student 1 test test\n"
                        + "Student 2 test test\n"
                        + "Student 4 test test";
        assertEquals(expected, formatter.formatGetStudentsByCourse(dto));
    }
    
    @Test
    void formatFindGroupsByStudentCount_shouldWorkCorrectly() {
        String expected = "Groups with such or equals students count:\n"
                        + "Group 1 qw-23\n"
                        + "Group 2 qr-53\n"
                        + "Group 3 as-72";
        assertEquals(expected, formatter.formatFindGroupsByStudentCount(dto));
    }
    
    @Test
    void formatEntityList_shouldWorkCorrectly() {
        String expected = "Student 1 test test\n"
                        + "Student 2 test test\n"
                        + "Student 4 test test";
        assertEquals(expected, formatter.formatEntityList(testStudents));
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