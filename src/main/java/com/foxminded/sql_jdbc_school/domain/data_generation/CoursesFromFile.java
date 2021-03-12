package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.foxminded.sql_jdbc_school.domain.DomainException;
import com.foxminded.sql_jdbc_school.domain.entity.Course;

public class CoursesFromFile implements EntityGeneration<Course> {
    
    private static final Path COURSES_DATA_PATH = Path
                                .of("src", "main", "resources", "Courses.txt");

    @Override
    public List<Course> generate() {
        String file = readFile();
        List<Course> courses = new ArrayList<>();
        String[] coursesData = file.split(";");
        for (int i = 0; i < coursesData.length; i++) {
            String courseData = coursesData[i];
            courseData = courseData.strip();
            String[] courseDataAsArray = courseData.split("[|]");
            courses.add(new Course(courseDataAsArray[0], courseDataAsArray[1]));
        }
        return courses;
    }
    
    private String readFile() {
        try {
            return Files.readString(COURSES_DATA_PATH);
        } catch (IOException e) {
            throw new DomainException(e);
        }   
    }
}