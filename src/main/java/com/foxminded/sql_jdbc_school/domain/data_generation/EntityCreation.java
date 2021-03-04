package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.util.List;

public interface EntityCreation<E> {
    
    List<E> create() throws Exception;
}