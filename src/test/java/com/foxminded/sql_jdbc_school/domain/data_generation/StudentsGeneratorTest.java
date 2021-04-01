package com.foxminded.sql_jdbc_school.domain.data_generation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.foxminded.sql_jdbc_school.domain.entity.Student;

class StudentsGeneratorTest {
    
    private static final Path FIRST_NAMES = Path.of("src", "test", "resources", "First_names.txt" );
    private static final Path LAST_NAMES = Path.of("src", "test", "resources", "Last_names.txt" );
    
    @Test
    void generate_shouldGenerateRightQuantityOfStudents() {
        StudentsGenerator generator = new StudentsGenerator();
        List<Student> students = generator.generate(FIRST_NAMES, LAST_NAMES);
        assertEquals(20, students.size());
    }
    
    @Test
    void generate_shouldGenerateStudentsWithCorrectNames() throws IOException {
        List<String> firstNames = null;
        List<String> lastNames = null;
        
        try(Stream<String> file = Files.lines(FIRST_NAMES)){
            firstNames = file.collect(Collectors.toList());
        }
        
        try(Stream<String> file = Files.lines(LAST_NAMES)){
            lastNames = file.collect(toList());
        }
        
        final List<String> firstNamesForStream = firstNames;
        final List<String> lastNamesForStream = lastNames;
        
        StudentsGenerator generator = new StudentsGenerator();
        List<Student> students = generator.generate(FIRST_NAMES, LAST_NAMES);
        
        students.forEach(s -> assertTrue(firstNamesForStream.stream()
                                                            .anyMatch(t -> t.equals(s.getFirstName()))));
        
        students.forEach(s -> assertTrue(lastNamesForStream.stream()
                                                            .anyMatch(t -> t.equals(s.getLastName()))));
    }
}