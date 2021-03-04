package com.foxminded.sql_jdbc_school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Course;

public class CourseDao {
    
    private static final CourseDao INSTANCE = new CourseDao();
    
    private CourseDao() {
        
    }
    
    public static CourseDao getInstance() {
        return INSTANCE;
    }
    
    private static final String SAVE_SQL = """
            INSERT INTO courses
            (course_name, course_description)
            VALUES
            (?, ?);
            """;
    
    public List<Course> saveAll(List<Course> courses) throws SQLException {
        Connection connection = null;
        PreparedStatement save = null;
        try {
            connection = ConnectionManager.open();
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
            return courses;
        }catch (Exception e){
            if(connection != null) {
                connection.rollback();
            }
            throw e;
        }finally {
            if(connection != null) {
                connection.close();
            }
            if(save != null) {
                save.close();
            }
        }
    }
}