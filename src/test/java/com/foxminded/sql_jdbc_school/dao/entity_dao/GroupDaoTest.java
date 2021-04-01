package com.foxminded.sql_jdbc_school.dao.entity_dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.foxminded.sql_jdbc_school.dao.TablesCreation;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.domain.entity.Group;

@TestInstance(Lifecycle.PER_CLASS)
class GroupDaoTest {
    
    private GroupDao groupDao = GroupDao.getInstance();

    @BeforeEach
    private void prepareTables() {
        TablesCreation creation = new TablesCreation();
        creation.create();
    }
    
    @Test
    void getAll_shouldWorkCorrectly() throws SQLException {
        List<Group> expected = retriveTestGroups();
        expected.get(0).setId(1);
        expected.get(1).setId(2);
        expected.get(2).setId(3);
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate("""
                    INSERT INTO groups
                    (id, group_name)
                    VALUES
                    (1, 'testName'),
                    (2, 'testName'),
                    (3, 'testName');""");
        }   
        assertEquals(expected, groupDao.getAll());   
    }
      
    @Test
    void saveAll_shouldWorkCorrectly() {
        List<Group> testGroups = retriveTestGroups();
        groupDao.saveAll(testGroups);
        assertEquals(testGroups, groupDao.getAll());
    }
    
    @Test
    void save_shouldWorkCorrectly() {
        Group testGroup = new Group("testName");
        groupDao.save(testGroup);
        assertEquals(testGroup, groupDao.getAll().get(0));
    }
    
    @Test
    void getByID_shouldWorkCorrectly() {
        List<Group> testGroups = insertTestGroups();
        assertEquals(testGroups.get(1), groupDao.getById(2).get());
    }
    
    @Test
    void update_shouldWorkCorrectly() {
        List<Group> testGroups = insertTestGroups();
        Group changedGroup = testGroups.get(1);
        changedGroup.setName("changedName");
        groupDao.update(changedGroup);
        assertEquals(changedGroup, groupDao.getById(changedGroup.getId()).get());
    }
    
    @Test
    void updateAll_shouldWorkCorrectly() {
        List<Group> testGroups = insertTestGroups();
        testGroups.forEach(s -> s.setName("changedName"));
        groupDao.updateAll(testGroups);
        assertEquals(testGroups, groupDao.getAll());
    }
    
    @Test
    void deleteById_shouldWorkCorrectly() {
        List<Group> testGroups = insertTestGroups();
        testGroups.remove(1);
        groupDao.deleteById(2);
        assertEquals(testGroups, groupDao.getAll());
    }
    
    @Test
    void getAllByStudentsQuantity_shouldWorkCorrectly() throws SQLException {
        List<Group> testGroups = insertTestGroups();
        insertTestStudents();
        int groupId1Count = 5;
        int groupId2Count = 7;
        int groupId3Count = 10;
        assertEquals(testGroups, groupDao.getAllByStudentsQuantity(groupId3Count));
        testGroups.remove(2);
        assertEquals(testGroups, groupDao.getAllByStudentsQuantity(groupId2Count));
        assertEquals(testGroups, groupDao.getAllByStudentsQuantity(groupId2Count + 1));
        testGroups.remove(1);
        assertEquals(testGroups, groupDao.getAllByStudentsQuantity(groupId1Count));
        assertEquals(new ArrayList<>(), groupDao.getAllByStudentsQuantity(groupId1Count - 1));
    }
    
    private void insertTestStudents() throws SQLException {
        String sql = """
                INSERT INTO students
                (group_id, first_name, last_name)
                VALUES
                (1, 'test', 'test'),
                (2, 'test', 'test'),
                (1, 'test', 'test'),
                (1, 'test', 'test'),
                (2, 'test', 'test'),
                (1, 'test', 'test'),
                (1, 'test', 'test'),
                (2, 'test', 'test'),
                (3, 'test', 'test'),
                (2, 'test', 'test'),
                (2, 'test', 'test'),
                (3, 'test', 'test'),
                (2, 'test', 'test'),
                (2, 'test', 'test'),
                (3, 'test', 'test'),
                (3, 'test', 'test'),
                (3, 'test', 'test'),
                (3, 'test', 'test'),
                (3, 'test', 'test'),
                (3, 'test', 'test'),
                (3, 'test', 'test'),
                (3, 'test', 'test');""";
        
        try(Connection connection = ConnectionManager.get();
                Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        }
        
    }

    private List<Group> retriveTestGroups() {
        List<Group> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            result.add(new Group("testName"));
        }
        return result;
    }
    
    private List<Group> insertTestGroups() {
        return groupDao.saveAll(retriveTestGroups());
    }
}