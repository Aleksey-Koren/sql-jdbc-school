package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.foxminded.sql_jdbc_school.dao.DaoRuntimeException;
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
    
    private static final String GET_ALL_BY_STUDENTS_QUANTITY = """
            SELECT  g.id, g.group_name, count(s.id) student_id
            FROM "groups" g 
                JOIN students s ON g.id = s.group_id
            GROUP BY g.id
            HAVING count(s.id) <= ?;
            """;
    
    public List<Group> saveAll(List<Group> groups) {
        Connection connection = null;
        PreparedStatement save = null;
        try {
            connection = ConnectionManager.get();
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
            connection.setAutoCommit(true);
            return groups;
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
    
//    a. Find all groups with less or equals student count
    public List<Group> getAllByStudentsQuantity(int quantity) {
        List<Group> result = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
             PreparedStatement get = connection.prepareStatement(GET_ALL_BY_STUDENTS_QUANTITY)){
            get.setInt(1, quantity);
            ResultSet resultSet = get.executeQuery();
            while(resultSet.next()) {
                result.add(new Group(resultSet.getInt("id"),
                                     resultSet.getString("group_name")));
            }       
        }catch(SQLException e) {
            throw new DaoRuntimeException(e);
        }        
        return result;
    }
}