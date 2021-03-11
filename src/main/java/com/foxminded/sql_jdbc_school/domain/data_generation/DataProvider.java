package com.foxminded.sql_jdbc_school.domain.data_generation;

import java.sql.SQLException;

public interface DataProvider {
    
    void provide() throws SQLException;
    
}