package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.foxminded.sql_jdbc_school.dao.DAOException;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Course;

public class CourseDao extends EntityDao<Course, Integer> {
    
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
    
    
    private static final String GET_ALL = """
            SELECT id,
                   course_name,
                   course_description
            FROM courses;
            """;  
    
    private static final String DELETE_SQL = """
            DELETE FROM courses
            WHERE id = ?;
            """;
        
    private static final String GET_COURSES_BY_STUDENT_ID = """
            SELECT c.id ,
                    course_name,
                    course_description
            FROM courses c
                JOIN students_courses sc ON c.id = sc.course_id
            WHERE sc.student_id = ?
            ORDER BY id;
            """;
    
    private static final String GET_COURSES_WITHOUT_STUDENT_ID = """
            (SELECT id,
                    course_name,
                    course_description
            FROM courses)
            EXCEPT
            (SELECT c.id ,
                    course_name,
                    course_description
            FROM courses c
                JOIN students_courses sc ON c.id = sc.course_id
            WHERE sc.student_id = ?)
            ORDER BY id;
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
            throw new DAOException(e);
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
            throw new DAOException(e);
        } 
    }

    @Override
    public boolean deleteById(Integer id) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement delete = connection.prepareStatement(DELETE_SQL)){
            delete.setInt(1, id);
            return delete.executeUpdate() > 0;
        }catch(SQLException e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Optional<Course> getById(Integer id) {
        String getById = GET_ALL.replace(";", " WHERE id = ?;");
        try(Connection connection = ConnectionManager.get();
                PreparedStatement get = connection.prepareStatement(getById)){
                get.setInt(1, id);
                ResultSet resultSet = get.executeQuery();
                return resultSet.next() ?
                        Optional.of(createFromResultSet(resultSet)) : Optional.empty();
            } catch (SQLException e) {
                throw new DAOException(e);
            }
    }
    
    @Override
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
        }catch (Exception e) {
            if(connection != null) {
                processRollback(connection);
            }
            throw new DAOException(e);
        } finally {
            if(save != null) {
                processClose(save);
            }
            if(connection != null) {
                processClose(connection); 
            }        
        }
    }
    
    @Override
    public void updateAll(List<Course> courses) {
        Connection connection = null;
        PreparedStatement update = null;
        try {
            connection = ConnectionManager.get();
            update = connection.prepareStatement(UPDATE_SQL);
            connection.setAutoCommit(false);
            
            for(Course course : courses) {
                update.setString(1, course.getName());
                update.setString(2, course.getDescription());
                update.setInt(3, course.getId());
                update.executeUpdate();
            }       
            
            connection.commit();
            connection.setAutoCommit(true);          
        }catch(Exception e){
            if(connection != null) {
                processRollback(connection);
                }         
            throw new DAOException(e);           
        }finally {
            if(update != null) {
                processClose(update);
            }
            if(connection != null) {
                processClose(connection); 
            }         
        }
    }
    
    @Override
    public List<Course> getAll(){
        List<Course> result = new ArrayList<>();
        String getAllOrdered = GET_ALL.replace(";", " ORDER BY id;");
        try(Connection connection = ConnectionManager.get();
            PreparedStatement get = connection.prepareStatement(getAllOrdered)){
            ResultSet resultSet = get.executeQuery();
            while(resultSet.next()) {
                result.add(createFromResultSet(resultSet));
            }
            return result;
        }catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    public List<Course> getCoursesByStudentId(Integer studentId){
        List<Course> result = new ArrayList<>();
        try(Connection connection = ConnectionManager.get();
            PreparedStatement get = connection.prepareStatement(GET_COURSES_BY_STUDENT_ID)){
            get.setInt(1, studentId);
            ResultSet resultSet = get.executeQuery();
            while(resultSet.next()) {
                result.add(createFromResultSet(resultSet));
            }
            return result;
        }catch (Exception e) {
            throw new DAOException(e);
        }
    }
    
    public List<Course> getCoursesStudentDoesNotHave(Integer studentId){
        List<Course> result = new ArrayList<>();
        try(Connection connection = ConnectionManager.get();
            PreparedStatement get = connection.prepareStatement(GET_COURSES_WITHOUT_STUDENT_ID)){
            get.setInt(1, studentId);
            ResultSet resultSet = get.executeQuery();
            while(resultSet.next()) {
                result.add(createFromResultSet(resultSet));
            }
            return result;
        }catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private Course createFromResultSet(ResultSet resultSet) throws SQLException {
        return new Course(resultSet.getInt("id"),
                          resultSet.getString("course_name"),
                          resultSet.getString("course_description"));
    }
}