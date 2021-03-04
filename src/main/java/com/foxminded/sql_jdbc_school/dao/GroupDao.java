package com.foxminded.sql_jdbc_school.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Group;

public class GroupDao {
    
    private static final GroupDao INSTANCE = new GroupDao(); 

    private GroupDao() {
        
    }
    
    public static GroupDao getInstance() {
        return INSTANCE;
    }
    
    private static final String SAVE_SQL = """
            INSERT INTO groups
            (group_name)
            VALUES
            (?);
            """;
    
    public List<Group> saveAll(List<Group> groups) throws SQLException{
        Connection connection = null;
        PreparedStatement save = null;
        try {
            connection = ConnectionManager.open();
            save = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
            connection.setAutoCommit(false);
            for (Group group : groups) {
                save.setString(1, group.getName());
                save.executeUpdate();
                ResultSet resultSet = save.getGeneratedKeys();
                if(resultSet.next()) {
                    int id =resultSet.getInt("id");
                    group.setId(id);
                }                
            }
            connection.commit();
            return groups;
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