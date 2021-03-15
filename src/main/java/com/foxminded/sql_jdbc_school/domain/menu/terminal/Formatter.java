package com.foxminded.sql_jdbc_school.domain.menu.terminal;

import com.foxminded.sql_jdbc_school.domain.entity.Student;

public interface Formatter {

    String formatMainMenu();

    String formatAddStudent(Student student);

    String formatDefault();

}