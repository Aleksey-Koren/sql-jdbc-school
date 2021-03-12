package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EntityDao <T, ID> {
    
    T save(T entity);
    
    void update(T entity);
    
    boolean deleteById(ID id);
    
    Optional <T> getById(ID id);
    
    List<T> saveAll(List<T> entities) throws SQLException;
    
    void updateAll(List<T> entities) throws SQLException;
}