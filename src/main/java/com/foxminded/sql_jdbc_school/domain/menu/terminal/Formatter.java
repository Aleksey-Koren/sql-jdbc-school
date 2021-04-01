package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import java.util.List;

import com.foxminded.sql_jdbc_school.dto.MenuDto;

public interface Formatter {
    
    public static final String STUDENTS_COUNT_PROMPT = "Enter students count (integer value)\n"
                                                     + "Enter \"back\" to return to main menu";
    
    public static final String NO_SUCH_GROUPS = "There is no groups with such or less students count";
    
    public static final String ENTER_INTEGER = "You have to enter integer value!";
    
    public static final String CHOSE_FROM_LIST = "You should chose from the list!";
    
    public static final String NO_SUCH_ID = "There is no such ID in school.";
    
    public static final String ID_SHOULD_BE_INT = "ID should be integer!";

    String formatMainMenu();

    String formatAddStudent(MenuDto dto);

    String formatDefault();

    String formatDeleteStudentById(MenuDto dto);

    String formatExit();

    String formatAddStudentToCourse(MenuDto dto);

    String formatDeleteStudentFromCourse(MenuDto dto);

    String formatGetStudentsByCourse(MenuDto dto);

    <T> String formatEntityList(List<T> entities);

    String formatFindGroupsByStudentCount(MenuDto dto);
    
    default String formatNewNamePrompt(String nameType) {
        return "Enter " + nameType + " (2 to 30 latin letters)" + "\n"
             + "Enter \"back\" - to return to main menu";
    }
    
    default String formatEntityFromListPrompt(String entityList, String entityTypeName) {
        return "===========================================================\n"  
             +  entityList + "\n"            
             + "===========================================================\n"               
             + "Enter " + entityTypeName +  " id from a list (integer value)\n"                 
             + "Enter \"back\" to return to main menu";
    }
}