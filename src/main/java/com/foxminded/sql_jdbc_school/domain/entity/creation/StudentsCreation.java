package com.foxminded.sql_jdbc_school.domain.entity.creation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class StudentsCreation implements EntityCreation<Student> {
    
    private static final Path FIRST_NAMES_PATH = Path.of("src", "main", "resources", "First_names.txt");
    private static final Path LAST_NAMES_PATH = Path.of("src", "main", "resources", "Last_names.txt");
    
    @Override
    public List<Student> create() throws IOException, NoSuchAlgorithmException {
        List<String> firstNames = readFile(FIRST_NAMES_PATH);
        List<String> lastNames = readFile(LAST_NAMES_PATH);
        Random random = SecureRandom.getInstanceStrong();
        List<Student> students = new ArrayList<>();
        
        Stream.generate(() -> 1).
        limit(200).
        forEach(x -> students.add(new Student(firstNames.get(random.nextInt(20)),
                                              lastNames.get(random.nextInt(20))
                                              )));
        
        return students;
    }
    
    private List<String> readFile(Path path) throws IOException {
       try(Stream<String> stream = Files.lines(path)){
           return stream.collect(Collectors.toList());
       }
    }
}