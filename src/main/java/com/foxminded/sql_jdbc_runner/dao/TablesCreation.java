package com.foxminded.sql_jdbc_runner.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Stream;

import com.foxminded.sql_jdbc_runner.dao.util.ConnectionManager;

public class TablesCreation {

    private static final Path TABLE_CREATION = Path.of("src", "main", "resources"
                                                            , "sql", "TablesCreation.sql");
    public void create() {     
        try(Connection connection = ConnectionManager.open();
            PreparedStatement createTables = connection.prepareStatement(getScript())) {
            createTables.executeUpdate();
        } catch (SQLException e) {
            throw new DaoRuntimeException("Exception in tables creation", e);
        }
    }
    
    private String getScript() {
        String sql;
        try(Stream<String> lines = Files.lines(TABLE_CREATION)) {
            sql = lines.reduce("", (a,b) -> a + b);
            return sql;
        } catch (IOException e) {
            throw new DaoRuntimeException("Can't read sql script", e);
        } 
    }
}