package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import java.util.List;

import com.foxminded.sql_jdbc_school.dto.MenuDto;

public interface Formatter {

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
}