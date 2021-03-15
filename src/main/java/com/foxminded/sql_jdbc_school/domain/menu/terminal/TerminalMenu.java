package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import com.foxminded.sql_jdbc_school.domain.DomainException;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class TerminalMenu {
    
    private Processor processor;
    private Formatter formatter;
    
    public TerminalMenu (Processor processor, Formatter formatter) {
        this.processor = processor;
        this.formatter = formatter;
    }
    
    public void run() { 
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            String userInput = processor.receveFromUser(reader, formatter.formatMainMenu());
            while(!userInput.equals("exit")){
                
                switch(userInput) {   
                case "1" -> {
                    Optional<Student> student = processor.addStudent(reader);
                    if(student.isPresent()) {
                        System.out.println(formatter.formatAddStudent(student.get()));
                    }
                }
                default -> System.out.println(formatter.formatDefault());
                }
                
                userInput = processor.receveFromUser(reader, formatter.formatMainMenu());
            }
        } catch (IOException e) {
            throw new DomainException(e);
        }
    }
}