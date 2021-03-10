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
    private static final String DELETE_SQL = """
            DELETE FROM students_courses
            WHERE student_id = ?
            AND course_id = ?;
            """;
    
    public static StudentsCoursesDao getInstance() {
        return INSTANCE;
    }
    
//    e. Add a student to the course (from a list) 
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
                throw new DaoRuntimeException(e);
            }      
        }
    }
    
//    f. Remove the student from one of his or her courses
    public boolean deleteStudentFromCourse(int studentId, int courseId ) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement delete = connection.prepareStatement(DELETE_SQL)){
            delete.setInt(1, studentId);
            delete.setInt(2, courseId);
            return delete.executeUpdate() > 0;
        }catch(SQLException e) {
            throw new DaoRuntimeException(e);
        }
    }
}