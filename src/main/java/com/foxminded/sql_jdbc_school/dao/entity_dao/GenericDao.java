package com.foxminded.sql_jdbc_school.dao.entity_dao;

import java.util.Optional;

public interface GenericDao <T> {
    
    T save(T entity);
    
    void update(T entity);
    
    boolean deleteById(int id);
    
    Optional <T> getById(int id);
}
