package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.foxminded.sql_jdbc_school.domain.DomainException;
import com.foxminded.sql_jdbc_school.dto.MenuDto;

public class TerminalMenu {
    
    private Processor processor;
    private Formatter formatter;
    
    public TerminalMenu (Processor processor, Formatter formatter) {
        this.processor = processor;
        this.formatter = formatter;
    }
    
    public void run() { 
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            String userInput;
            do {
                userInput = processor.requestToUser(reader, formatter.formatMainMenu());
                switch(userInput) {
                
                case "1" -> {
                    MenuDto dto = processor.processAddNewStudent(reader);
                    if (dto.isCanceled()) {
                        continue;
                    }
                    System.out.println(formatter.formatAddStudent(dto));
                }
             
                case "2" -> {
                    MenuDto dto = processor.processDeleteById(reader);
                    if (dto.isCanceled()) {
                        continue;
                    }
                    System.out.println(formatter.formatDeleteStudentById(dto));
                }
                
                case "3" -> {
                    MenuDto dto = processor.processAddStudentCourse(reader);
                    if (dto.isCanceled()) {
                        continue;
                    }
                    System.out.println(formatter.formatAddStudentToCourse(dto));
                    
                }
                
                case "4" -> {
                    MenuDto dto = processor.processDeleteStudentFromCourse(reader);
                    if (dto.isCanceled()) {
                        continue;
                    }
                    System.out.println(formatter.formatDeleteStudentFromCourse(dto));
                }
                
                case "5" -> {
                    MenuDto dto = processor.processGetStudentsByCourse(reader);
                    if (dto.isCanceled()) {
                        continue;
                    }
                    System.out.println(formatter.formatGetStudentsByCourse(dto));
                }
                
                case "6" -> {
                    MenuDto dto = processor.processFindGroupsByStudentCount(reader);
                    if (dto.isCanceled()) {
                        continue;
                    }
                    System.out.println(formatter.formatFindGroupsByStudentCount(dto));
                }
                
                case "exit" -> {
                    System.out.println(formatter.formatExit());
                }
                
                default -> System.out.println(formatter.formatDefault());
                }                
            }while(!userInput.equals("exit"));
        } catch (IOException e) {
            throw new DomainException(e);
        }
    }
}