package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.util.List;

public interface EntityGeneration<E> {
    
    List<E> generate() throws Exception;
}