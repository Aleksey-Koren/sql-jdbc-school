package com.foxminded.sql_jdbc_school.domain.data_generation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.foxminded.sql_jdbc_school.domain.entity.Course;

class CourseGeneratorTest {
    
    private static final Path TEST_FILE = Path.of("src", "test", "resources", "Courses.txt");

    @Test
    void generate_shouldGenerateRightCoursesFromFile() {
        List<Course> expectedCourses = Arrays.asList(new Course("math", "This is\r\n"
                                                                      + "description of\r\n"
                                                                      + "math course"),
                                                     new Course ("chemistry", "This is\r\n"
                                                                            + "description of\r\n"
                                                                            + "chemistry course"));
        CourseGenerator generator = new CourseGenerator();
        List<Course> courses = generator.generate(TEST_FILE);
        assertEquals(expectedCourses, courses);
    }
}