package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import com.foxminded.sql_jdbc_school.dao.DaoRuntimeException;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class StudentsCoursesDao {

    private static final StudentsCoursesDao INSTANCE = new StudentsCoursesDao();
    
    private StudentsCoursesDao() {
        
    }
    
    private static final String SAVE_SQL = """
            INSERT INTO students_courses
            (student_id, course_id)
            VALUES
            (?, ?);
            """;
    
    public static StudentsCoursesDao getInstance() {
        return INSTANCE;
    }
    
    public void addStudentToCourses(Student student, Set<Integer> courseId) {
        Connection connection = null;
        PreparedStatement save = null;
        try {
            connection = ConnectionManager.get();
            save = connection.prepareStatement(SAVE_SQL);
            connection.setAutoCommit(false);
            for(Integer id : courseId) {
                save.setInt(1, student.getId());
                save.setInt(2, id);
                save.executeUpdate();
            }
            
            connection.commit();
            
        }catch(Exception e) {
            if(connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    throw new DaoRuntimeException(e1);
                }
            }
            
            throw new DaoRuntimeException(e);
        }finally {
            try {
                if(save != null) {
                    save.close();
                }
                if(connection != null) {
                    connection.close();
                }       
            }catch(SQLException e) {
                throw new DaoRuntimeException();
            }      
        }
    }       
}