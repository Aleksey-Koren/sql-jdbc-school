package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.foxminded.sql_jdbc_school.dao.entity_dao.StudentDao;
import com.foxminded.sql_jdbc_school.domain.DomainException;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class Processor {
    
    private static final Pattern NAME = Pattern.compile("^[a-zA-Z]{2,30}$");
    private static final String BACK = "back";
    private static final StudentDao STUDENT_DAO = StudentDao.getInstance();
    
    public Optional<Student> addStudent(BufferedReader reader) {
        String firstName = "";
        String lastName = "";
        do {
            firstName = receveFromUser(reader, "Enter first name (2 to 30 letters)\n"
                                             + "Enter \"back\" - to return to main menu");
            if(firstName.equals(BACK)) {
                return Optional.empty();
            }
        }while(!(match(NAME, firstName)));
        
        do {
            lastName = receveFromUser(reader, "Enter last name (2 to 30 letters)\n"
                                            + "Enter \"back\" - to return to main menu");
            if(lastName.equals(BACK)) {
                return Optional.empty();
            }
        }while(!(match(NAME, lastName)));
        
        return Optional.of(STUDENT_DAO.save(new Student(firstName, lastName)));
    }

    public String receveFromUser(BufferedReader reader, String prompt) {
        try {
            System.out.println(prompt);
            return reader.readLine();
        } catch (IOException e) {
            throw new DomainException(e);
        }
    }
    
    private boolean match(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}
