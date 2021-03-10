package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.foxminded.sql_jdbc_school.dao.DaoRuntimeException;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Student;

public class StudentDao {
    
    private static final StudentDao INSTANCE = new StudentDao();
    
    private StudentDao() {
        
    }
    
    private static final String SAVE_SQL = """
            INSERT INTO students 
            (group_id, first_name, last_name)
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
                   students.last_name,
                   students.course_name
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
    
    public static StudentDao getInstance() {
        return INSTANCE;
    }
    
//    c. Add new student
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
            throw new DaoRuntimeException(e);
        }
    }
    
    public List<Student> saveAll(List<Student> students) {
        Connection connection = null;
        PreparedStatement save = null;
        try{
            connection = ConnectionManager.get();
            save = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            connection.setAutoCommit(false);
            
            for (int i = 0; i < students.size(); i++) {
                Student current = students.get(i);
                save.setObject(1, students.get(i).getGroupId());
                save.setString(2, current.getFirstName());
                save.setString(3, current.getLastName());
                save.executeUpdate();
                ResultSet resultSet = save.getGeneratedKeys();
                if(resultSet.next()) {
                    int id = resultSet.getInt("id");
                    current.setId(id);
                }
                students.set(i, current);
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return students;
        } catch (Exception e) {
            if(connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    throw new DaoRuntimeException(e1);
                }
            }
            throw new DaoRuntimeException(e);       
        } finally {
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
    
    public void updateAll(List<Student> students){
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
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    throw new DaoRuntimeException(e1);
                }
            }         
            throw new DaoRuntimeException(e);           
        }finally {
            try {
                if(update != null) {
                    update.close();              
                }
                if(connection != null) {
                    connection.close();   
                }
            }catch(SQLException e) {
                throw new DaoRuntimeException(e);
            }           
        }
    }

//    d. Delete student by STUDENT_ID
    public boolean deleteById (int id) {
        try (Connection connection = ConnectionManager.get();
                PreparedStatement delete = connection.prepareStatement(DELETE_SQL)){
            delete.setInt(1, id);
            return delete.executeUpdate() > 0;
        }catch(SQLException e) {
            throw new DaoRuntimeException(e);
        }
    }
    
//    b. Find all students related to course with given name
    public List<Student> getAllByCourseName (String courseName) {
        List<Student> result = new ArrayList<>();
        try(Connection connection = ConnectionManager.get();
            PreparedStatement get = connection.prepareStatement(GET_ALL_BY_COURSE_NAME)){
            get.setString(1, courseName);
            ResultSet resultSet = get.executeQuery();
            while(resultSet.next()) {
                result.add(new Student(resultSet.getInt("id"),
                                       resultSet.getObject("group_id", Integer.class),
                                       resultSet.getString("first_name"),
                                       resultSet.getString("last_name")));
            }
            }catch (SQLException e) {
            throw new DaoRuntimeException(e);
        }
        return result;
    }
}

















