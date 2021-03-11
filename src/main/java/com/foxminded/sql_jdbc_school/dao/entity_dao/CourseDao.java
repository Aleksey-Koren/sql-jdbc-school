package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import com.foxminded.sql_jdbc_school.dao.DaoRuntimeException;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Course;
import com.foxminded.sql_jdbc_school.domain.entity.Group;

public class CourseDao implements GenericDao<Course> {
    
    private static final CourseDao INSTANCE = new CourseDao();
           
    private static final String SAVE_SQL = """
            INSERT INTO courses
            (course_name, course_description)
            VALUES
            (?, ?);
            """;
    
    private static final String UPDATE_SQL = """
            UPDATE courses
            SET course_name = ?,
                course_description = ?              
            WHERE id = ?;
            """;
    private static final String GET_BY_ID = """
            SELECT id,
                   course_name,
                   course_description
            FROM courses
            WHERE id = ?;       
            """;  
    private static final String DELETE_SQL = """
            DELETE FROM courses
            WHERE id = ?;
            """; 
    
    private CourseDao() {
        
    }
    
    public static CourseDao getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Course save(Course course) {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement save = 
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                save.setString(1, course.getName());
                save.setString(2, course.getDescription());
                save.executeUpdate();
                ResultSet resultSet = save.getGeneratedKeys();
                if(resultSet.next()) {
                    course.setId(resultSet.getInt("id"));
                }
                return course;
        }catch (SQLException e){
            throw new DaoRuntimeException(e);
        }
    }

    @Override
    public void update(Course course) {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement update = 
                        connection.prepareStatement(UPDATE_SQL)) {
                update.setString(1, course.getName());
                update.setString(2, course.getDescription());
                update.setInt(3, course.getId());         
                update.executeUpdate();
        }catch (SQLException e){
            throw new DaoRuntimeException(e);
        } 
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement delete = connection.prepareStatement(DELETE_SQL)){
            delete.setInt(1, id);
            return delete.executeUpdate() > 0;
        }catch(SQLException e) {
            throw new DaoRuntimeException(e);
        }
    }

    @Override
    public Optional<Course> getById(int id) {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement get = connection.prepareStatement(GET_BY_ID)){
                get.setInt(1, id);
                ResultSet resultSet = get.executeQuery();
                return resultSet.next() ?
                        Optional.of(createFromResultSet(resultSet)) : Optional.empty();
            } catch (SQLException e) {
                throw new DaoRuntimeException(e);
            }
    }
    
    public List<Course> saveAll(List<Course> courses) throws SQLException {
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
                connection.rollback();
            }     
            throw new DaoRuntimeException(e);
        }finally {
            if(save != null) {
                save.close();
            }
            if(connection != null) {
                connection.close();
            }    
        }
    }

    private Course createFromResultSet(ResultSet resultSet) throws SQLException {
        return new Course(resultSet.getInt("id"),
                          resultSet.getString("course_name"),
                          resultSet.getString("course_description"));
    }
}