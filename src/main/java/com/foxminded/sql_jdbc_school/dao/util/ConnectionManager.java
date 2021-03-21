package com.foxminded.sql_jdbc_school.dao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

import com.foxminded.sql_jdbc_school.dao.PoolConnection;
import com.foxminded.sql_jdbc_school.dao.DAOException;


public final class ConnectionManager {
    
    private static final String URL = PropertiesUtil.get("db.url");
    private static final String USERNAME = PropertiesUtil.get("db.username");
    private static final String PASSWORD = PropertiesUtil.get("db.password");
    private static final int CONNECTIONS_QUANTITY =
            Integer.parseInt(PropertiesUtil.get("connections.quantity"));
    
    private static final ArrayBlockingQueue<PoolConnection> POOL = new ArrayBlockingQueue<>(CONNECTIONS_QUANTITY);
    
    private ConnectionManager() {

    }
  
    static {
        for (int i = 0; i < CONNECTIONS_QUANTITY; i++) {
            POOL.add(new PoolConnection(open()));
        }
    }
    
    
    public static int getPoolSize() {
        return POOL.size();
    }
    
    public static PoolConnection get() {
        try {
            return POOL.take();
        } catch (InterruptedException e) {
            throw new DAOException(e);
        }
    }
    
    public static void put(PoolConnection connection) {
        try {
            POOL.put(connection);
        } catch (InterruptedException e) {
            throw new DAOException(e);
        }
    }
    
    private static Connection open(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            return connection;
        } catch (SQLException e) {
            throw new DAOException(e);
        } 
    }
}