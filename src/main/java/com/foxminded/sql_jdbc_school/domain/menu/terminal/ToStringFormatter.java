package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import java.util.List;

import com.foxminded.sql_jdbc_school.dto.MenuDto;

public class ToStringFormatter implements Formatter {

    @Override
    public String formatMainMenu() {
        return """
                ===========================================================
                MAIN MENU
                Enter:
                \"1\" - to add new student.
                \"2\" - to delete student by ID.
                \"3\" - to add student to course.
                \"4\" - to delete student from course.
                \"5\" - to find all students related to course.
                \"6\" - to find all groups with less or equals student count.
                \"exit\" - to exit program.""";
    }
    
    @Override
    public String formatAddStudent(MenuDto dto) {
        return dto.getStudent().get().toString() + " has been added";
    }
    
    @Override
    public  String formatDeleteStudentById(MenuDto dto) {
        return dto.getStudent().get().toString() + " "
                + "has been deleted";
    }

    @Override
    public String formatAddStudentToCourse(MenuDto dto) {
        String result = dto.getStudent().get().toString() + "\nhas been added to" + "\n" 
                        + dto.getCourse().get().toString();
        return result;
    }
    
    @Override
    public String formatDeleteStudentFromCourse(MenuDto dto) {
        return dto.getStudent().get().toString() + "\nhas been deleted from" + "\n"
                        + dto.getCourse().get().toString();
    }
    
    @Override
    public String formatGetStudentsByCourse(MenuDto dto) {
        return dto.getCourse().get().toString() 
                + "\nStudents on this course:\n\n"
                + formatEntityList(dto.getStudents());
    }
    
    @Override
    public String formatFindGroupsByStudentCount(MenuDto dto) {
        String result = "Groups with such or equals students count:\n"
                      + formatEntityList(dto.getGroups());
        return result;
    }
    
    @Override
    public String formatDefault() {
        return "Incorrect parameter was entered! Please Try again.";
    }
    
    @Override
    public String formatExit() {
        return "Application is closed";
    }

    @Override
    public <T> String formatEntityList(List<T> entities) {
        StringBuilder builder = new StringBuilder("");
        entities.forEach(s -> builder.append(s.toString() + "\n"));
        return builder.toString().strip();
    }
}