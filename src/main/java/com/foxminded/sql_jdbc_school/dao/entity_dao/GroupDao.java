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
import com.foxminded.sql_jdbc_school.domain.entity.Group;

public class GroupDao extends EntityDao<Group, Integer> {
    
    private static final GroupDao INSTANCE = new GroupDao();
 
    private static final String SAVE_SQL = """
            INSERT INTO groups
            (group_name)
            VALUES
            (?);
            """;
    
    private static final String UPDATE_SQL = """
            UPDATE groups
            SET group_name = ?
            WHERE id = ?;
            """;
    
    private static final String GET_BY_ID = """
            SELECT id,
                   group_name
            FROM groups
            WHERE id = ?;       
            """;  
    
    private static final String GET_ALL = """
            SELECT id,
                   group_name
            FROM groups
            ORDER BY id;
            """;
    
    private static final String DELETE_SQL = """
            DELETE FROM groups
            WHERE id = ?;
            """;
    
    private static final String GET_ALL_BY_STUDENTS_QUANTITY = """
            SELECT  g.id, g.group_name, count(s.id) student_id
            FROM groups g 
                JOIN students s ON g.id = s.group_id
            GROUP BY g.id
            HAVING count(s.id) <= ?
            ORDER BY g.id;
            """;
    
    private GroupDao() {
        
    }
    
    public static GroupDao getInstance() {
        return INSTANCE;
    }
    
    @Override
    public Group save(Group group) {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement save = 
                        connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
                save.setString(1, group.getName());
                save.executeUpdate();
                ResultSet resultSet = save.getGeneratedKeys();
                if(resultSet.next()) {
                    group.setId(resultSet.getInt("id"));
                }
                return group;
        }catch (SQLException e){
            throw new DAOException(e);
        }
    }

    @Override
    public void update(Group group) {
        try(Connection connection = ConnectionManager.get();
                PreparedStatement update = 
                        connection.prepareStatement(UPDATE_SQL)) {
                update.setString(1, group.getName());
                update.setInt(2, group.getId());
                update.executeUpdate();
        }catch (SQLException e){
            throw new DAOException(e);
        }        
    }
    
    @Override
    public Optional<Group> getById(Integer id) {
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
                    int id = resultSet.getInt("id");
                    group.setId(id);
                }                
            }
            connection.commit();
            connection.setAutoCommit(true);
            return groups;
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
    public void updateAll(List<Group> groups) {
        Connection connection = null;
        PreparedStatement update = null;
        try {
            connection = ConnectionManager.get();
            update = connection.prepareStatement(UPDATE_SQL);
            connection.setAutoCommit(false);
            
            for(Group group : groups) {
                update.setString(1, group.getName());
                update.setInt(2, group.getId());
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
    public List<Group> getAll() {
        List<Group> result = new ArrayList<>();
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
            throw new DAOException(e);
        }        
        return result;
    }

    private Group createFromResultSet(ResultSet resultSet) throws SQLException {
        return new Group(resultSet.getInt("id"),
                         resultSet.getString("group_name"));
    }
}