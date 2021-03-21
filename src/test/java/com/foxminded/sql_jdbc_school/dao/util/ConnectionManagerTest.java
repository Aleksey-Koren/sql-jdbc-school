package com.foxminded.sql_jdbc_school.dao.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import com.foxminded.sql_jdbc_school.dao.DAOException;
import com.foxminded.sql_jdbc_school.dao.PoolConnection;
import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;
import com.foxminded.sql_jdbc_school.dao.util.PropertiesUtil;

class ConnectionManagerTest {
    
    @Test
    void get_shouldReturnConnection() {
        
        try(Connection testedConnection = ConnectionManager.get()){
            assertTrue(testedConnection instanceof Connection); 
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    @Test
    void pool_shouldContainsAsManyConnectionsAsDefinedInProperties() {
        int ProperiesConnectionsQuantity = Integer.parseInt(PropertiesUtil.get("connections.quantity"));
        assertEquals(ProperiesConnectionsQuantity, ConnectionManager.getPoolSize());
    }
    
    @Test
    void put_shouldBackConnectionToPool() {
        int startPoolSize = ConnectionManager.getPoolSize();
        PoolConnection connection = ConnectionManager.get();
        assertEquals(ConnectionManager.getPoolSize(), startPoolSize - 1);
        ConnectionManager.put(connection);
        assertEquals(ConnectionManager.getPoolSize(), startPoolSize);
    }
}