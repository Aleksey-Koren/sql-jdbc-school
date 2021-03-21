package com.foxminded.sql_jdbc_school.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import com.foxminded.sql_jdbc_school.dao.util.ConnectionManager;

class PoolConnectionTest {
    
    @Test
    void close_shouldPutConnectionIntoThePool() {
        int startPoolSize = ConnectionManager.getPoolSize();
        Connection connection = ConnectionManager.get();
        assertEquals(ConnectionManager.getPoolSize(), startPoolSize - 1);
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        assertEquals(ConnectionManager.getPoolSize(), startPoolSize);
    }
}
