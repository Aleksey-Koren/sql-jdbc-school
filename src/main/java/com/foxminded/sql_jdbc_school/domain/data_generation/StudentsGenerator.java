package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foxminded.sql_jdbc_school.dao.util.PropertiesUtil;
import com.foxminded.sql_jdbc_school.domain.DomainException;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class StudentsGenerator implements EntityGeneration<Student> {
    
    private static final Path FIRST_NAMES_DEFAULT = Path
                            .of("src", "main", "resources", "First_names.txt");
    private static final Path LAST_NAMES_DEFAULT = Path
                            .of("src", "main", "resources", "Last_names.txt");
    private static final int QUANTITY_OF_STUDENTS = 
            Integer.parseInt(PropertiesUtil.get("quantity.of.students"));
    
    @Override
    public List<Student> generate(){
        List<Student> students = new ArrayList<>(); 
        List<String> firstNames = readFile(FIRST_NAMES_DEFAULT);
        List<String> lastNames = readFile(LAST_NAMES_DEFAULT);
        
        Random random = new Random();
        for(int i = 0; i < QUANTITY_OF_STUDENTS; i++) {
            students.add(new Student(firstNames.get(random.nextInt(firstNames.size())),
                                     lastNames.get(random.nextInt(lastNames.size()))
                                     ));
        }        
        return students;
    }
    
    public List<Student> generate(Path...paths) {
        List<Student> students = new ArrayList<>(); 
        List<String> firstNames = readFile(paths[0]);
        List<String> lastNames = readFile(paths[1]);
        
        Random random = new Random();
        for(int i = 0; i < QUANTITY_OF_STUDENTS; i++) {
            students.add(new Student(firstNames.get(random.nextInt(firstNames.size())),
                                     lastNames.get(random.nextInt(lastNames.size()))
                                     ));
        }        
        return students;
    }
    
    private List<String> readFile(Path path) {
       try(Stream<String> stream = Files.lines(path)){
           return stream.collect(Collectors.toList());
       } catch (IOException e) {
           throw new DomainException(e);
       }
    }
}