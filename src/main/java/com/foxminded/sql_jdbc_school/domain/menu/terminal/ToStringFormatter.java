package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class ToStringFormatter implements Formatter {

    @Override
    public String formatMainMenu() {
        return """
                Enter:
                \"1\" - to add new student.
                \"exit\" - to exit program.""";
    }
    
    @Override
    public String formatAddStudent(Student student) {
        return student.toString() + " has added";
    }

    @Override
    public String formatDefault() {
        return "Incorrect parameter was entered! Please Try again.";
    }
}