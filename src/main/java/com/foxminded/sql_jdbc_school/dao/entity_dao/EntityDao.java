package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.foxminded.sql_jdbc_school.dao.DAOException;

public abstract class EntityDao <T, D> {
    
    public abstract T save(T entity);
    
    public abstract void update(T entity);
    
    public abstract boolean deleteById(D id);
    
    public abstract Optional <T> getById(D id);
    
    public abstract List<T> getAll();
    
    public abstract List<T> saveAll(List<T> entities);
    
    public abstract void updateAll(List<T> entities);
    
    protected void processRollback(Connection connection) {
        try {
            connection.rollback();
        }catch(SQLException e) {
            throw new DAOException(e);
        }
    }
    
    protected void processClose (AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}