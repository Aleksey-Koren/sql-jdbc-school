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
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class StudentDao implements EntityDao<Student, Integer> {
    
    private static final StudentDao INSTANCE = new StudentDao();
   
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
    
    private static final String GET_BY_ID = """
            SELECT id,
                   group_id,
                   first_name,
                   last_name
            FROM students
            WHERE id = ?;
            """;
    
    private static final String GET_ALL_BY_COURSE_NAME = """
            SELECT students.id,
                   students.group_id,
                   students.first_name,
                   students.last_name, 
            FROM (SELECT s.id,
                         s.group_id,
                         s.first_name,
                         s.last_name,
                         c.course_name
                  FROM students s
                      JOIN students_courses sc ON s.id = sc.student_id
                          JOIN courses c ON sc.course_id = c.id) students
            WHERE students.course_name = ?;
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
        try(Connection connection = ConnectionManager.get();
            PreparedStatement get = connection.prepareStatement(GET_BY_ID)){
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
    
    public List<Student> saveAll(List<Student> students) throws SQLException {
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
        }catch (SQLException e) {
            if(connection != null) {
                connection.rollback();
            }
            throw new DAOException(e);
        } finally {
            if(save != null) {
                save.close();
            }
            if(connection != null) {
                connection.close(); 
            }        
        }
    }
    
    public void updateAll(List<Student> students) throws SQLException{
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
        }catch(SQLException e){
            if(connection != null) {
                connection.rollback();
                }         
            throw new DAOException(e);           
        }finally {
            if(update != null) {
                update.close();              
            }
            if(connection != null) {
                connection.close();   
            }         
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
    
    private Student createFromResultSet(ResultSet resultSet) throws SQLException {
        return new Student(resultSet.getInt("id"),
                resultSet.getObject("group_id", Integer.class),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"));
    }
}