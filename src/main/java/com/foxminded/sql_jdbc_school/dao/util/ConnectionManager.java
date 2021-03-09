package com.foxminded.sql_jdbc_school.dao.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;

import com.foxminded.sql_jdbc_school.dao.PoolConnection;
import com.foxminded.sql_jdbc_school.dao.DaoRuntimeException;

public final class ConnectionManager {
    
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final ArrayBlockingQueue<PoolConnection> POOL = new ArrayBlockingQueue<>(5);
    
    private ConnectionManager() {

    }
    
    static {
        for (int i = 0; i < 5; i++) {
            POOL.add(new PoolConnection(open()));
        }
    }
    
    public static PoolConnection get() {
        try {
            return POOL.take();
        } catch (InterruptedException e) {
            throw new DaoRuntimeException(e);
        }
    }
    
    public static void put(PoolConnection connection) {
        try {
            POOL.put(connection);
        } catch (InterruptedException e) {
            throw new DaoRuntimeException(e);
        }
    }
    
    private static Connection open(){
        Connection connection = null;
        try {            
            connection = DriverManager.getConnection(PropertiesUtil.get(URL_KEY),
                                              PropertiesUtil.get(USERNAME_KEY),
                                              PropertiesUtil.get(PASSWORD_KEY));
            return connection;
        } catch (SQLException e) {
            throw new DaoRuntimeException(e);
        } 
    }
}