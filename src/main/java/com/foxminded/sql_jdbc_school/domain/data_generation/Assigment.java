package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.sql.SQLException;
import java.util.List;

import com.foxminded.sql_jdbc_school.domain.entity.Student;
import com.foxminded.sql_jdbc_school.dto.SchoolDto;

public interface Assigment {
    
    List<Student> assignGroups(SchoolDto dto) throws SQLException;
    
    void assignCourses(SchoolDto dto) throws SQLException;

}