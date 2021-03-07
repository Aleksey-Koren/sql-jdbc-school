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
    
    public static StudentDao getInstance() {
        return INSTANCE;
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}