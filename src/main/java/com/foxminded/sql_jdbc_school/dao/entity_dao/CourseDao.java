package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.foxminded.sql_jdbc_school.dao.DaoRuntimeException;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Course;

public class CourseDao {
    
    private static final CourseDao INSTANCE = new CourseDao();
           
    private static final String SAVE_SQL = """
            INSERT INTO courses
            (course_name, course_description)
            VALUES
            (?, ?);
            """;
    
    private CourseDao() {
        
    }
    
    public static CourseDao getInstance() {
        return INSTANCE;
    }
    
    public List<Course> saveAll(List<Course> courses) {
        Connection connection = null;
        PreparedStatement save = null;
        try {
            connection = ConnectionManager.get();
            save = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            connection.setAutoCommit(false);
            for (Course course : courses) {
                save.setString(1, course.getName());
                save.setString(2, course.getDescription());
                save.executeUpdate();
                ResultSet resultSet = save.getGeneratedKeys();
                if(resultSet.next()) {
                    int id =resultSet.getInt("id");
                    course.setId(id);
                }                
            }
            connection.commit();
            connection.setAutoCommit(true);
            return courses;
        }catch (Exception e){
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
}