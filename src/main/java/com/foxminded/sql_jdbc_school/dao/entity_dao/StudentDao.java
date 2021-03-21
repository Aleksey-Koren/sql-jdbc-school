package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.foxminded.sql_jdbc_school.dao.DAOException;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class StudentDao extends EntityDao<Student, Integer> {
    
    private static final StudentDao INSTANCE = new StudentDao();
    
    private static final String GET_ALL = """
            SELECT id,
                   group_id,
                   first_name,
                   last_name
            FROM students
            ORDER BY id;
            """;
   
    private static final String SAVE_SQL = """
            INSERT INTO students 
            (group_id,first_name, last_name)
            VALUES
            (?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE students
            SET group_id = ?,
                first_name = ?,
                last_name = ?
            WHERE id = ?;
            """;
    
    private static final String DELETE_SQL = """
            DELETE FROM students
            WHERE id = ?;
            """;

    
    private static final String GET_ALL_BY_COURSE_NAME = """
            SELECT students.id,
                   students.group_id,
                   students.first_name,
                   students.last_name 
            FROM (SELECT s.id,
                         s.group_id,
                         s.first_name,
                         s.last_name,
                         c.course_name
                  FROM students s
                      JOIN students_courses sc ON s.id = sc.student_id
                          JOIN courses c ON sc.course_id = c.id) students
            WHERE students.course_name = ?
            ORDER BY students.id;
            """;
  
    private static final String LINK_STUDENT_COURSE = """
            INSERT INTO students_courses
            (student_id, course_id)
            VALUES
            (?, ?);
            """; 
    
    private static final String DELETE_STUDENT_COURSE = """
            DELETE FROM students_courses
            WHERE student_id = ?
            AND course_id = ?;
            """;
                
    private StudentDao() {
        
    }
    
    public static StudentDao getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Student save (Student student) {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement save = 
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                save.setObject(1, student.getGroupId());
                save.setString(2, student.getFirstName());
                save.setString(3, student.getLastName());
                save.executeUpdate();
                ResultSet resultSet = save.getGeneratedKeys();
                if(resultSet.next()) {
                    student.setId(resultSet.getInt("id"));
                }
                return student;
        }catch (SQLException e){
            throw new DAOException(e);
        }
    }
    
    @Override
    public void update(Student student) {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement update = 
                        connection.prepareStatement(UPDATE_SQL)) {
                update.setObject(1, student.getGroupId());
                update.setString(2, student.getFirstName());
                update.setString(3, student.getLastName());
                update.setInt(4, student.getId());
                update.executeUpdate();
        }catch (SQLException e){
            throw new DAOException(e);
        }   
    }
    
    @Override
    public Optional<Student> getById(Integer id) {
        String getById = GET_ALL.replaceFirst("ORDER BY id;", """
                    
                                                   WHERE id = ?;""");
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
    public boolean deleteById (Integer id) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement delete = connection.prepareStatement(DELETE_SQL)){
            delete.setInt(1, id);
            return delete.executeUpdate() > 0;
        }catch(SQLException e) {
            throw new DAOException(e);
        }
    }
    
    @Override
    public List<Student> saveAll(List<Student> students) {
        Connection connection = null;
        PreparedStatement save = null;
        try{
            connection = ConnectionManager.get();
            save = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            connection.setAutoCommit(false);

            for(Student student : students) {
                save.setObject(1, student.getGroupId());
                save.setString(2, student.getFirstName());
                save.setString(3, student.getLastName());
                save.executeUpdate();
                ResultSet resultSet = save.getGeneratedKeys();
                if(resultSet.next()) {
                    student.setId(resultSet.getInt("id"));
                }
            }          
            connection.commit();
            connection.setAutoCommit(true);
            return students;
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
    public void updateAll(List<Student> students) {
        Connection connection = null;
        PreparedStatement update = null;
        try {
            connection = ConnectionManager.get();
            update = connection.prepareStatement(UPDATE_SQL);
            connection.setAutoCommit(false);
            
            for(Student student : students) {
                update.setObject(1, student.getGroupId());
                update.setString(2, student.getFirstName());
                update.setString(3, student.getLastName());
                update.setInt(4, student.getId());
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
    public List<Student> getAll(){
        List<Student> result = new ArrayList<>();
        try(Connection connection = ConnectionManager.get();
                PreparedStatement get = connection.prepareStatement(GET_ALL)){
                ResultSet resultSet = get.executeQuery();
                while(resultSet.next()) {
                    result.add(createFromResultSet(resultSet));
                }
                return result;
            }catch (Exception e) {
                throw new DAOException(e);
            }
    }
    
    public List<Student> getAllByCourseName (String courseName) {
        List<Student> result = new ArrayList<>();
        try(Connection connection = ConnectionManager.get();
            PreparedStatement get = connection.prepareStatement(GET_ALL_BY_COURSE_NAME)){
            get.setString(1, courseName);
            ResultSet resultSet = get.executeQuery();
            while(resultSet.next()) {
                result.add(createFromResultSet(resultSet));
            }
            }catch (SQLException e) {
            throw new DAOException(e);
        }
        return result;
    }
    
    public void addStudentToCourses(Integer studentId, Set<Integer> courseId) {
        Connection connection = null;
        PreparedStatement save = null;
        try {
            connection = ConnectionManager.get();
            save = connection.prepareStatement(LINK_STUDENT_COURSE);
            connection.setAutoCommit(false);
            for(Integer id : courseId) {
                save.setInt(1, studentId);
                save.setInt(2, id);
                save.executeUpdate();
            }      
            connection.commit();
            connection.setAutoCommit(true);
        }catch(Exception e) {
            if(connection != null) {
                processRollback(connection);
            }
            throw new DAOException(e);
        }finally {
            if(save != null) {
                processClose(save);
            }
            if(connection != null) {
                processClose(connection); 
            }              
        }
    }
    
    public boolean deleteStudentFromCourse(int studentId, int courseId) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement delete = connection.prepareStatement(DELETE_STUDENT_COURSE)){
            delete.setInt(1, studentId);
            delete.setInt(2, courseId);
            return delete.executeUpdate() > 0;
        }catch(SQLException e) {
            throw new DAOException(e);
        }
    }
        
    private Student createFromResultSet(ResultSet resultSet) throws SQLException {
        return new Student(resultSet.getInt("id"),
                resultSet.getObject("group_id", Integer.class),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"));
    }
}