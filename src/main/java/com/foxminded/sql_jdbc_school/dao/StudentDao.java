package com.foxminded.sql_jdbc_school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
    
    public static StudentDao getInstance() {
        return INSTANCE;
    }
    
    public List<Student> saveAll(List<Student> students) throws SQLException {
        List<Student> studentsAfter = new ArrayList<>();
        Connection connection = null;
        PreparedStatement save = null;
        try{
            connection = ConnectionManager.open();
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
                studentsAfter.add(current);
            }
            connection.commit();
            return studentsAfter;
        } catch (Exception e) {
            if(connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if(connection != null) {
                connection.close();
            }
            
            if(save != null) {
                save.close();
            }
        }
    }
}